package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/*
* Class which works with network part
 */
public class ConnectionManager implements Runnable {

    /*
    * @param meta the .torrent file
    * @param DU a class working with file system
    * @param peersAddresses a list of addresses of peers to connect, the first one is the address of this client
    * @param seeder whether this client is seeder or not
     */
    public ConnectionManager(MetainfoFile meta, DownloadUploadManager DU, List<InetSocketAddress> peersAddresses, boolean seeder, int id) {
        this.infoHash = meta.getInfoHash();
        this.DU = DU;
        this.piecesInfo = new PiecesAndBlocksInfo((int) meta.getFileLen(), (int) meta.getPieceLen(), BLOCK_LEN);
        this.messagesReceiver = new MessagesReceiver(meta.getInfoHash(), DU, piecesInfo);
        int fromIdx, toIdx;
//        int middle = Math.ceilDiv(piecesInfo.getPiecesNum(), 2);
        int middle = piecesInfo.getPiecesNum() / 2;
        if (id == 0) {
            fromIdx = 0;
            toIdx = middle;
        } else if (id == 1) {
            fromIdx = middle;
            toIdx = piecesInfo.getPiecesNum();
        } else {
            fromIdx = 0;
            toIdx = piecesInfo.getPiecesNum();
        }
        System.out.println("FROM: " + fromIdx);
        System.out.println("TO: " + toIdx);
        this.iam = new Peer(null, piecesInfo, seeder, fromIdx, toIdx);

        try {
            server = ServerSocketChannel.open();
            selector = Selector.open();
            server.bind(peersAddresses.get(0));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e) {
            System.err.println("Couldn't init connection manager");
            e.printStackTrace();
            releaseResources();
            Thread.currentThread().interrupt();
            return;
        }

        if (!seeder) {
            connectToPeers(peersAddresses.subList(1, peersAddresses.size()));
            if (connections.isEmpty()) {
                System.out.println("Peers with this addresses aren't working");
                Thread.currentThread().interrupt();
                return;
            }

            for (Peer peer : connections) {
                messagesReceiver.addMsgToQueue(peer, new Handshake(infoHash, peer.getId()));
            }
        }
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int ready = selector.selectNow();
                if (ready == 0) {
                    continue;
                }
            }
            catch (IOException e) {
                System.err.println("Can't select");
                e.printStackTrace();
                return;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                if (!key.isValid()) {
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

            Integer idx;
            while ((idx = DU.getSuccessfulCheck()) != null) {
                notifyPeers(idx);
                System.out.println(iam.getBitset().getPiecesHas());

                if (iam.getBitset().isHasAllPieces()) {
                    System.out.println("---------------------------------------------");
                    System.out.println("Have all the messages, downloading completed!");
                    System.out.println("---------------------------------------------");
                    Thread.currentThread().interrupt();
                }
            }

            while ((idx = DU.getUnsuccessfulCheck()) != null) {
                handleNotVerifiedPiece(idx);
            }

            if (connections.isEmpty()) {
                System.out.println("Connections list is empty, stopped");
                break;
            }
        }

        DU.addTask(Task.createStopTask());
        releaseResources();
    }

    private void connectToPeers(List<InetSocketAddress> peersAddresses) {
        for (InetSocketAddress address : peersAddresses) {
            try {
                SocketChannel channel = SocketChannel.open(address);
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                Peer peer = new Peer(channel, piecesInfo);
                connections.add(peer);
                System.out.println("Connected to " + peer);
            } catch (IOException e) {
                System.out.println("Peer with address: " + address + " isn't working");
            }
        }
    }

    private void releaseResources() {
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

    private void handleNotVerifiedPiece(int idxToClear) {
        iam.getBitset().clearPiece(idxToClear);
    }

    private Peer findPeer(SocketChannel remoteChannel) {
        for (Peer peer : connections) {
            if (peer.getChannel().equals(remoteChannel)) {
                return peer;
            }
        }
        return null;
    }

    /*
    * Accepts a connection and adds own handshake to queue to the connected peer
     */
    private void accept(SelectionKey key) {
        try {
            server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            System.out.println("Connect from " + channel.getLocalAddress());
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


    /*
    * Reads data from peer and handle messages
    * @return false if peer is disconnected while reading or if I/O errors occur
     */
    private boolean readFromPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        assert peer != null;
        if (!iam.getHandshaked().contains(peer)) {
            if (!messagesReceiver.readHS(peer)) {
                return closeConnection(key, peer);
            }
        }

        if (!messagesReceiver.readFrom(peer)) {
            System.out.println(peer + " disconnected");
            return closeConnection(key, peer);
        }
        IMessage msg;
        while ((msg = messagesReceiver.getMsgFrom(peer)) != null) {
            messagesReceiver.handleMsg(peer, iam, msg);
        }
        return true;
    }

    private boolean closeConnection(SelectionKey key, Peer peer) {
        peer.closeConnection();
        connections.remove(peer);
        key.cancel();
        return false;
    }

    /*
    * Sends messages to a peer
     */
    private void sendToPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        assert peer != null;
        //add messages to peer from DU
        IMessage msg;
        while ((msg = DU.getOutgoingMsg(peer)) != null) {
            messagesReceiver.addMsgToQueue(peer, msg);
        }
        //send messages to peer
        while ((msg = messagesReceiver.getMsgTo(peer)) != null) {
            if (!messagesSender.sendMsg(peer, msg)) {
                System.out.println(peer + " disconnected");
                closeConnection(key, peer);
                return;
            }
            System.out.println("Wrote to " + peer + ", type of msg: " + msg.getType());
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
