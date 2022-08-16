package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ConnectionManager implements Runnable {
    public ConnectionManager(MetainfoFile meta, DownloadUploadManager DU, List<InetSocketAddress> peers, boolean leecher) {
        this.infoHash = meta.getInfoHash();
        this.DU = DU;
        this.leecher = leecher;
        piecesInfo = new PiecesAndBlocksInfo((int) meta.getFileLen(), (int) meta.getPieceLen(), Constants.BLOCK_LEN);
        try {
            selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
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

        if (leecher) {
            List<SocketChannel> channels = connectToPeers(peers.subList(1, peers.size()));
            if (channels == null) {
                Thread.currentThread().interrupt();
                return;
            }
            sendHStoPeers(channels, meta.getInfoHash());
        }
        iam = new Peer(null, piecesInfo);
        //todo SOMEWHERE TO SET ALL PIECES TO TRUE IF IAM IS SEEDER
        System.out.println("Initialized connection manager");
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

    private void sendHStoPeers(List<SocketChannel> channels, byte[] infoHash) {
        for (SocketChannel channel : channels) {
            Peer peer = new Peer(channel, piecesInfo);
            connections.add(peer);
            addMsgToQueue(peer, new Handshake(infoHash, peer.getId()));
        }
    }

    @Override
    public void run() {
        System.out.println("Connection manager is running");
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
                    readFromPeer(key);
                }
                if (key.isWritable()) {
                    sendToPeer(key);
                }
            }
            selector.selectedKeys().clear();

            if (connections.isEmpty()) {
                System.out.println("Connections list is empty, returning");
                return;
            }

            while (!DU.getSuccessfulCheck().isEmpty()) {
                Integer idxHave = DU.getSuccessfulCheck().poll();
                assert idxHave != null;
                for (Peer peer : connections) {
                    addMsgToQueue(peer, new ProtocolMessage(MessagesTypes.HAVE, Util.convertToByteArr(idxHave)));
                }

                if (iam.isHasAllPieces()) {
                    DU.addTask(new Task(TaskType.STOP));
                    System.out.println("Have all the messages, download completed!");
                    return;
                }
            }
            while (!DU.getUnsuccessfulCheck().isEmpty()) {
                Integer idxToClear = DU.getUnsuccessfulCheck().poll();
                assert idxToClear != null;
                iam.clearPiece(idxToClear);
                for (Peer peer : connections) {
                    Message request = iam.createRequest(peer);
                    if (request == null) {
                        continue;
                    }
                    messagesToPeer.get(peer).add(request);
                }
            }
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

    private void addMsgToQueue(Peer peer, Message msg) {
        if (messagesToPeer.containsKey(peer)) {
            messagesToPeer.get(peer).add(msg);
            return;
        }
        Queue<Message> messages = new LinkedList<>();
        messages.add(msg);
        messagesToPeer.put(peer, messages);
    }

    private void accept(SelectionKey key) {
        System.out.println("ENTER ACCEPT");
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            Peer peer = new Peer(channel, piecesInfo);
            connections.add(peer);
            addMsgToQueue(peer, new Handshake(infoHash, iam.getId()));
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
        catch (IOException e) {
            System.err.println("Catch an exception while accepting a connection");
            e.printStackTrace();
        }
    }

    private void readFromPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        assert peer != null;
        if (!iam.getHandshaked().contains(peer)) {
            peer.getReadyMessages().add(peer.getRemoteHS());
        }

        peer.constructPeerMsg();
        while (!peer.getReadyMessages().isEmpty()) {
            Message msg = peer.getReadyMessages().poll();
            assert msg != null;
            handleMsg(peer, msg);
        }
    }


    //todo подумать, через кого отправлять
    private void sendToPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        assert peer != null;
        //todo mb also extract method
        if (DU.getOutgoingMsg().containsKey(peer)) {
            synchronized (DU.getOutgoingMsg().get(peer)) {
                if (!DU.getOutgoingMsg().get(peer).isEmpty()) {
                    messagesToPeer.get(peer).addAll(DU.getOutgoingMsg().get(peer));
                    DU.getOutgoingMsg().get(peer).clear();
                }
            }
        }

        while (!messagesToPeer.get(peer).isEmpty()) {
            Message msgToSend = messagesToPeer.get(peer).poll();
            assert msgToSend != null;
            peer.sendMsg(msgToSend);
            System.out.println("Wrote to " + peer + ", type of msg: " + msgToSend.getType());
        }
//        System.out.println("No more messages to " + peer);
    }

    private void handleMsg( Peer peer, Message msg) {
        System.out.println("Got message from " + peer);
        switch (msg.getType()) {
            case MessagesTypes.HANDSHAKE -> {
                System.out.println("HANDSHAKE");
                //спецом id пировский, т.к. трекера нет
                if (!Arrays.equals(msg.getMessage(), new Handshake(infoHash, peer.getId()).getMessage())) {
                    System.out.println("HS are different");
                    peer.closeConnection();
                    return;
                }
                iam.getHandshaked().add(peer);
                //mb move it to connection, add hs and interested
                if (leecher) {
                    addMsgToQueue(peer, new ProtocolMessage(MessagesTypes.INTERESTED));
                    peer.setInterested(true);
                }
            }
            case MessagesTypes.CHOKE -> {
                System.out.println("CHOKE");
                peer.setChoking(true);
            }
            case MessagesTypes.UNCHOKE -> {
                System.out.println("UNCHOKE");
                peer.setChoking(false);
                //todo подумать мб ещё припилить метод, который будет говорить, заинтересован ли я в пире, а то мб у него нет ничего
            }
            case MessagesTypes.INTERESTED -> {
                System.out.println("INTERESTED");
                addMsgToQueue(peer, new ProtocolMessage(MessagesTypes.UNCHOKE));
                addMsgToQueue(peer, new ProtocolMessage(MessagesTypes.BITFIELD, iam.getPiecesHas().toByteArray()));
                peer.setInteresting(true);
                peer.setChoked(false);
            }
            case MessagesTypes.NOT_INTERESTED -> {
                System.out.println("NOT INTERESTED");
                peer.setInteresting(false);
            }
            case MessagesTypes.HAVE -> {
                System.out.println("HAVE");
                peer.setPiece(Util.convertToInt(msg.getPayload()), true);
            }
            case MessagesTypes.BITFIELD -> {
                System.out.println("BITFIELD");
                if (msg.getMessage().length > 5) {
                    peer.setPiecesHas(msg.getPayload());
                }

                if (!iam.isHasAllPieces()) {
                    Message request = iam.createRequest(peer);
                    if (request == null) {
                        return;
                    }
                    addMsgToQueue(peer, request);
                }
            }
            case MessagesTypes.REQUEST -> {
                System.out.println("REQUEST");
                //A block is uploaded by a client when the client is not choking a peer,
                // and that peer is interested in the client
                if (peer.isChoked() || !peer.isInteresting()) {
                    System.out.println("Rejected request because " + peer + " is choked or not interesting");
                    return;
                }
                byte[] payload = msg.getPayload();
                if (payload != null) {
                    if (payload.length != 12) {
                        return;
                    }
                    int idx = Util.convertToInt(Arrays.copyOfRange(payload, 0, 4));
                    int begin = Util.convertToInt(Arrays.copyOfRange(payload, 4, 8));
                    int len = Util.convertToInt(Arrays.copyOfRange(payload, 8, payload.length));
                    System.out.println("REQUESTED idx: " + idx + ", begin: " + begin + ", len: " + len);

                    DU.addTask(new Task(TaskType.SEND, idx, begin, len, peer));
                }
            }
            case MessagesTypes.PIECE -> {
                System.out.println("PIECE");
                //A block is downloaded by the client when the client is interested in a peer,
                // and that peer is not choking the client
                if (!peer.isInterested() || peer.isChoking()) {
                    System.out.println("Rejected piece because " + peer + " is choking or not interested");
                    return;
                }
                byte[] payload = msg.getPayload();
                if (payload != null) {
                    if (payload.length < 8) {
                        return;
                    }
                    int pieceIdx = Util.convertToInt(Arrays.copyOfRange(payload, 0, 4));
                    int begin = Util.convertToInt(Arrays.copyOfRange(payload, 4, 8));
                    byte[] blockData = Arrays.copyOfRange(payload, 8, payload.length);

                    System.out.println("PIECE idx: " + pieceIdx + ", begin: " + begin + ", blockData len: " + blockData.length);

                    DU.addTask(new Task(TaskType.SAVE, pieceIdx, begin, blockData));
                    int blockIdx = begin / piecesInfo.getBlockLen();
                    iam.setBlock(pieceIdx, blockIdx);

                    if (iam.isPieceFull(pieceIdx)) {
                        iam.setPiece(pieceIdx, true);
                        if (iam.isLastPiece(pieceIdx)) {
                            DU.addTask(new Task(TaskType.CHECK_HASH, pieceIdx, piecesInfo.getLastPieceLen()));
                        } else {
                            DU.addTask(new Task(TaskType.CHECK_HASH, pieceIdx, piecesInfo.getPieceLen()));
                        }
                    }

                    Message request = iam.createRequest(peer);
                    if (request == null) {
                        return;
                    }
                    addMsgToQueue(peer, request);
                }
            }
            case MessagesTypes.CANCEL -> {
                //todo
                //как отменять? где очередь сообщений должна быть?
                //нужен интерфейс, чтобы ещё и have всем отправлять
            }
        }
    }

    private final PiecesAndBlocksInfo piecesInfo;
    private final Map<Peer, Queue<Message>> messagesToPeer = new HashMap<>();
    private final List<Peer> connections = new ArrayList<>();
    private Peer iam;
    private Selector selector;
    private final byte[] infoHash;
    private final DownloadUploadManager DU;
    private boolean leecher = false;
}
