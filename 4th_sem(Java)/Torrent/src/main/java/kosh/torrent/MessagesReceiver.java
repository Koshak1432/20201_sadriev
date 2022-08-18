package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class MessagesReceiver implements IMessagesReceiver {

    public MessagesReceiver(byte[] infoHash, DownloadUploadManager DU, PiecesAndBlocksInfo piecesInfo, boolean seeder) {
        this.infoHash = infoHash;
        this.DU = DU;
        this.piecesInfo = piecesInfo;
        this.seeder = seeder;
    }

    @Override
    public IMessage getMsgTo(Peer peer) {
        return messagesToPeer.get(peer).poll();
    }

    @Override
    public void addMsgToQueue(Peer peer, IMessage msg) {
        System.out.println("ADDING " + msg.getType() + " MSG TO messagesToPeer");
        addToMessages(messagesToPeer, peer, msg);
    }

    private void addToMessages(Map<Peer, Queue<IMessage>> map, Peer peer, IMessage msg) {
        if (map.containsKey(peer)) {
            map.get(peer).add(msg);
            return;
        }
        Queue<IMessage> messages = new LinkedList<>();
        messages.add(msg);
        map.put(peer, messages);
    }

    @Override
    public void handleMsg(Peer sender, Peer receiver, IMessage msg) {
        System.out.println("Got message from " + sender + " , type: " + msg.getType());
        switch (msg.getType()) {
            case MessagesTypes.KEEP_ALIVE -> System.out.println("KEEP ALIVE");
            case MessagesTypes.HANDSHAKE -> {
                System.out.println("HANDSHAKE");
                //спецом id пировский, т.к. трекера нет
                if (! Arrays.equals(msg.getMessage(), new Handshake(infoHash, sender.getId()).getMessage())) {
                    System.out.println("HS are different");
                    sender.closeConnection();
                    return;
                }
                receiver.getHandshaked().add(sender);
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.BITFIELD, receiver.getPiecesHas().toByteArray()));
            }
            case MessagesTypes.CHOKE -> {
                System.out.println("CHOKE");
                sender.setChoking(true);
            }
            case MessagesTypes.UNCHOKE -> {
                System.out.println("UNCHOKE");
                IMessage request = receiver.createRequest(sender);
                if (request == null) {
                    System.out.println("request is null");
                    return;
                }
                System.out.println("added request from have handling");
                addMsgToQueue(sender, request);
                sender.setChoking(false);
            }
            case MessagesTypes.INTERESTED -> {
                System.out.println("INTERESTED");
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.UNCHOKE));
                sender.setInteresting(true);
                sender.setChoked(false);

            }
            case MessagesTypes.NOT_INTERESTED -> {
                System.out.println("NOT INTERESTED");
                sender.setInteresting(false);
            }
            case MessagesTypes.HAVE -> {
                System.out.println("HAVE");
                if (!receiver.isHasAllPieces()) {
                    System.out.println("have: " + Util.convertToInt(msg.getPayload()));
                    sender.setPiece(Util.convertToInt(msg.getPayload()), true);
                }
            }
            case MessagesTypes.BITFIELD -> {
                System.out.println("BITFIELD");
                if (msg.getMessage().length > 5) {
                    sender.setPiecesHas(msg.getPayload());
                }
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.INTERESTED));
                sender.setInterested(true);
            }
            case MessagesTypes.REQUEST -> {
//                System.out.println("REQUEST");
                //A block is uploaded by a client when the client is not choking a peer,
                //and that peer is interested in the client
                if (sender.isChoked() || !sender.isInteresting()) {
                    System.out.println("Rejected request because " + sender + " is choked or not interesting");
                    return;
                }
                handleRequest(sender, msg);
            }
            case MessagesTypes.PIECE -> {
//                System.out.println("PIECE");
                //A block is downloaded by the client when the client is interested in a peer,
                //and that peer is not choking the client
                if (!sender.isInterested() || sender.isChoking()) {
                    System.out.println("Rejected piece because " + sender + " is choking or not interested");
                    return;
                }
                handlePiece(sender, receiver, msg);
            }
            case MessagesTypes.CANCEL -> {
                //todo как отменять? где очередь сообщений должна быть?
            }
        }
    }

    private void addMsgFrom(Peer peer, IMessage msg) {
        addToMessages(messagesFromPeer, peer, msg);
    }

    @Override
    public boolean readHS(Peer peer) {
        int HSSize = 68;
        int infoHashIdx = 28;
        int peerIdIdx = infoHashIdx + 20;
        byte[] infoHash = new byte[20];
        byte[] peerId = new byte[20];
        ByteBuffer byteBuffer = ByteBuffer.allocate(HSSize);
        byteBuffer.limit(HSSize);
        int read;
        try {
            read = peer.getChannel().read(byteBuffer);
            if (read != HSSize) {
                System.err.println("Read less bytes than handshake size");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Couldn't read handshake");
            e.printStackTrace();
            return false;
        }
        byteBuffer.get(infoHashIdx, infoHash, 0, infoHash.length);
        byteBuffer.get(peerIdIdx, peerId, 0, peerId.length);
        peer.setId(peerId);
        addMsgFrom(peer, new Handshake(infoHash, peerId));
        return true;
    }

    @Override
    public boolean readFrom(Peer peer) {
        boolean able = readFromChannel(peer);
        if (!able) {
            //todo это что-то значит? отрубился пир
            return false;
        }
        while (hasFullMessage(getBytes(peer))) {
            addFullMessages(getBytes(peer), peer);
        }
        return true;
    }

    @Override
    public IMessage getMsgFrom(Peer peer) {
        if (messagesFromPeer.containsKey(peer)) {
            return messagesFromPeer.get(peer).poll();
        }
        return null;
    }

    private void handleRequest(Peer from, IMessage msg) {
        byte[] payload = msg.getPayload();
        if (payload != null) {
            if (payload.length != 12) {
                return;
            }
            int idx = Util.convertToInt(Arrays.copyOfRange(payload, 0, 4));
            int begin = Util.convertToInt(Arrays.copyOfRange(payload, 4, 8));
            int len = Util.convertToInt(Arrays.copyOfRange(payload, 8, payload.length));
            System.out.println("REQUESTED idx: " + idx + ", begin: " + begin + ", len: " + len);

            DU.addTask(Task.createExtractTask(idx, begin, len, from));
        }
    }

    private void handlePiece(Peer from, Peer to, IMessage msg) {
        System.out.println("handle piece from " + from);
        byte[] payload = msg.getPayload();
        if (payload != null) {
            if (payload.length < 8) {
                System.out.println("piece payload < 8");
                return;
            }
            int pieceIdx = Util.convertToInt(Arrays.copyOfRange(payload, 0, 4));
            int begin = Util.convertToInt(Arrays.copyOfRange(payload, 4, 8));
            byte[] blockData = Arrays.copyOfRange(payload, 8, payload.length);

            System.out.println("PIECE idx: " + pieceIdx + ", begin: " + begin + ", blockData len: " + blockData.length);

            DU.addTask(Task.createSaveTask(pieceIdx, begin, blockData));
            int blockIdx = begin / piecesInfo.getBlockLen();
            to.setBlock(pieceIdx, blockIdx);
            if (to.isPieceFull(pieceIdx)) {
                System.out.println("piece " + pieceIdx + " is full(handling), " + " set piece to true");
                to.setPiece(pieceIdx, true);
                int pieceLen = to.isLastPiece(pieceIdx) ? piecesInfo.getLastPieceLen() : piecesInfo.getPieceLen();
                DU.addTask(Task.createCheckTask(pieceIdx, pieceLen));
            }
            if (to.isHasAllPieces()) {
                System.out.println("have all the pieces(handling)");
                return;
            }

            IMessage request = to.createRequest(from);
            if (request == null) {
                System.out.println("request is null, sender: " + from);
                return;
            }
            addMsgToQueue(from, request);
        }
    }


    private List<Byte> getBytes(Peer peer) {
        if (!readBytes.containsKey(peer)) {
            readBytes.put(peer, new LinkedList<>());
        }
        return readBytes.get(peer);
    }
    private boolean readFromChannel(Peer peer) {
        int bytesToAllocate = piecesInfo.getBlockLen();
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read;
        try {
            read = peer.getChannel().read(buffer);
//            System.out.println("READ " + read + " bytes");
            if (read == -1) {
                return false;
            }
        } catch (IOException e) {
            System.out.println("Peer disconnected");
            return false;
        }

        List<Byte> bytes = getBytes(peer);
        for (int i = 0; i < read; ++i) {
            bytes.add(buffer.get(i));
        }
        buffer.clear();
        return true;
    }

    private boolean hasFullMessage(List<Byte> bytesList) {
        if (bytesList.isEmpty()) {
            return false;
        }
        int prefixLen = 4;
        byte[] length = new byte[prefixLen];
        for (int i = 0; i < prefixLen; ++i) {
            length[i] = bytesList.get(i);
        }
        int messageLen = Util.convertToInt(length);
        return bytesList.size() - prefixLen >= messageLen;
    }

    private void addFullMessages(List<Byte> bytesList, Peer peer) {
        int prefixLen = 4;
        byte[] length = new byte[prefixLen];
        byte[] id = new byte[1];
        for (int i = 0; i < prefixLen; ++i) {
            length[i] = bytesList.get(i);
        }
//        System.out.println("POSSIBLE ID: " + bytesList.get(4));
//        System.out.println("LENGTH IN ADD FULL MSG: " + Arrays.toString(length));
        bytesList.subList(0, prefixLen).clear();
//        System.out.println("id[0] is " + bytesList.get(0));
        id[0] = bytesList.get(0);
        bytesList.remove(0);
        int messageLen = Util.convertToInt(length);
        int idInt = id[0];
        int payloadLen = messageLen - 1;
        IMessage msg = new ProtocolMessage(idInt);
//        System.out.println("adding full msg from " + peer + " with id: " + idInt + " , payload len: " + payloadLen);
        if (payloadLen > 0) {
            byte[] payload = new byte[payloadLen];
            for (int i = 0; i < payloadLen; ++i) {
                payload[i] = bytesList.get(i);
            }
            bytesList.subList(0, payloadLen).clear();
            msg = new ProtocolMessage(idInt, payload);
        }
        addToMessages(messagesFromPeer, peer, msg);
    }


    private final Map<Peer, Queue<IMessage>> messagesToPeer = new HashMap<>(); //incoming (to send)
    private final Map<Peer, Queue<IMessage>> messagesFromPeer = new HashMap<>(); //outgoing (to handle)

    private final Map<Peer, List<Byte>> readBytes = new HashMap<>();
//    private final List<Byte> readBytes = new ArrayList<>();
    private final PiecesAndBlocksInfo piecesInfo;
    private final byte[] infoHash;
    private final boolean seeder;
    private final DownloadUploadManager DU;
}
