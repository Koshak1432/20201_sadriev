package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ConnectionManager implements Runnable {
    public ConnectionManager(MetainfoFile meta, DownloadUploadManager DU, List<InetSocketAddress> peers, boolean seeder) {
        this.infoHash = meta.getInfoHash();
        this.DU = DU;
        this.piecesInfo = new PiecesAndBlocksInfo((int) meta.getFileLen(), (int) meta.getPieceLen(), BLOCK_LEN);
        this.messagesReceiver = new MessagesReceiver(meta.getInfoHash(), DU, piecesInfo, seeder);
        this.iam = new Peer(null, piecesInfo, seeder);

        try {
            server = ServerSocketChannel.open();
            selector = Selector.open();
            server.bind(peers.get(0));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e) {
            System.err.println("Couldn't init connection manager");
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return;
        }

        if (!seeder) {
            List<SocketChannel> channels = connectToPeers(peers.subList(1, peers.size()));
            if (channels == null) {
                Thread.currentThread().interrupt();
                return;
            }
            addHSToPeers(channels, meta.getInfoHash());
        }
    }

    private List<SocketChannel> connectToPeers(List<InetSocketAddress> peers) {
        List<SocketChannel> channels = new ArrayList<>();
        try {
            for (InetSocketAddress address : peers) {
                SocketChannel channel = SocketChannel.open(address);
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                channels.add(channel);
                System.out.println("Connected to " + address);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return channels;
    }

    private void addHSToPeers(List<SocketChannel> channels, byte[] infoHash) {
        for (SocketChannel channel : channels) {
            Peer peer = new Peer(channel, piecesInfo);
            connections.add(peer);
            messagesReceiver.addMsgToQueue(peer, new Handshake(infoHash, peer.getId()));
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int ready;
            try {
                ready = selector.selectNow();
            }
            catch (IOException e) {
                System.err.println("Can't select");
                e.printStackTrace();
                return;
            }
            if (ready == 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                if (! key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                }
                if (key.isReadable()) {
                    if (!readFromPeer(key)) {
                        continue;
                    }
                }
                if (key.isWritable()) {
                    sendToPeer(key);
                }
            }
            selector.selectedKeys().clear();

            Integer idxHave, idxToClear;
            while ((idxHave = DU.getSuccessfulCheck()) != null) {
                notifyPeers(idxHave);

                if (iam.isHasAllPieces()) {
                    DU.addTask(Task.createStopTask());
                    System.out.println("Have all the messages, download completed!");
                    //todo можно апдейтнуть везде флаг сидера на тру, чтобы этот тоже остался и можно было с него грузить
                    closeConnections();
                    return;
                }
            }

            while ((idxToClear = DU.getUnsuccessfulCheck()) != null) {
                handleFailPiece(idxToClear);
            }

            if (connections.isEmpty()) {
                System.out.println("Connections list is empty, stopped");
                DU.addTask(Task.createStopTask());
                return;
            }
        }
    }

    private void closeConnections() {
        for (Peer peer : connections) {
            peer.closeConnection();
        }

        try {
            server.close();
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyPeers(int idxHave) {
        for (Peer peer : connections) {
            messagesReceiver.addMsgToQueue(peer, new ProtocolMessage(MessagesTypes.HAVE, Util.convertToByteArr(idxHave)));
        }
    }

    private void handleFailPiece(int idxToClear) {
        iam.clearPiece(idxToClear);
        for (Peer peer : connections) {
            IMessage request = iam.createRequest(peer);
            if (request == null) {
                continue;
            }
            messagesReceiver.addMsgToQueue(peer, request);
        }
    }

    private Peer findPeer(SocketChannel remoteChannel) {
        for (Peer peer : connections) {
            if (peer.getChannel().equals(remoteChannel)) {
                return peer;
            }
        }
        return null; //never
    }

    private void accept(SelectionKey key) {
        try {
            server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            Peer peer = new Peer(channel, piecesInfo);
            connections.add(peer);
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            messagesReceiver.addMsgToQueue(peer, new Handshake(infoHash, iam.getId()));
        }
        catch (IOException e) {
            System.err.println("Couldn't accept a connection");
            e.printStackTrace();
        }
    }

    private boolean readFromPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        assert peer != null;
        if (!iam.getHandshaked().contains(peer)) {
            if (!messagesReceiver.readHS(peer)) {
                peer.closeConnection();
                connections.remove(peer);
                key.cancel();
                return false;
            }
        }

        if (!messagesReceiver.readFrom(peer)) {
            System.out.println(peer + " disconnected");
            peer.closeConnection();
            connections.remove(peer);
            key.cancel();
            return false;
        }
        IMessage msg;
        while ((msg = messagesReceiver.getReadyMsg()) != null) {
            messagesReceiver.handleMsg(peer, iam, msg);
        }
        return true;
    }

    private void sendToPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        assert peer != null;
        IMessage msgFromDU;
        while ((msgFromDU = DU.getOutgoingMsg(peer)) != null) {
            messagesReceiver.addMsgToQueue(peer, msgFromDU);
        }

        IMessage msgToSend;
        while ((msgToSend = messagesReceiver.getMsgTo(peer)) != null) {
            messagesSender.sendMsg(peer, msgToSend);
            System.out.println("Wrote to " + peer + ", type of msg: " + msgToSend.getType());
        }
    }


    private Selector selector;
    private final PiecesAndBlocksInfo piecesInfo;
    private final IMessagesReceiver messagesReceiver;
    private final IMessagesSender messagesSender = new MessagesSender();
    private final List<Peer> connections = new ArrayList<>();
    private final Peer iam;
    private final byte[] infoHash;
    private final IDownloadUploadManager DU;

    private ServerSocketChannel server;

    public final int BLOCK_LEN = 16 * 1024;
}
