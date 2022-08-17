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

    public Message pollMessage(Peer peer) {
        return messagesToPeer.get(peer).poll();
    }


    public void addMsgToQueue(Peer peer, Message msg) {
        if (messagesToPeer.containsKey(peer)) {
            messagesToPeer.get(peer).add(msg);
            return;
        }
        Queue<Message> messages = new LinkedList<>();
        messages.add(msg);
        messagesToPeer.put(peer, messages);
    }

    public void handleMsg(Peer sender, Peer receiver, Message msg) {
        System.out.println("Got message sender " + sender);
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
                if (!seeder) {
                    addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.INTERESTED));
                    sender.setInterested(true);
                }
            }
            case MessagesTypes.CHOKE -> {
                System.out.println("CHOKE");
                sender.setChoking(true);
            }
            case MessagesTypes.UNCHOKE -> {
                System.out.println("UNCHOKE");
                sender.setChoking(false);
            }
            case MessagesTypes.INTERESTED -> {
                System.out.println("INTERESTED");
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.UNCHOKE));
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.BITFIELD, receiver.getPiecesHas().toByteArray()));
                sender.setInteresting(true);
                sender.setChoked(false);

            }
            case MessagesTypes.NOT_INTERESTED -> {
                System.out.println("NOT INTERESTED");
                sender.setInteresting(false);
            }
            case MessagesTypes.HAVE -> {
                System.out.println("HAVE");
                if (!seeder) {
                    sender.setPiece(Util.convertToInt(msg.getPayload()), true);
                }
            }
            case MessagesTypes.BITFIELD -> {
                System.out.println("BITFIELD");
                if (msg.getMessage().length > 5) {
                    sender.setPiecesHas(msg.getPayload());
                }
                //receiver -- кому пришло данное сообщение, sender -- от кого
                if (!receiver.isHasAllPieces()) {
                    Message request = receiver.createRequest(sender);
                    if (request == null) {
                        System.out.println("request is null");
                        return;
                    }
                    addMsgToQueue(sender, request);
                }
            }
            case MessagesTypes.REQUEST -> {
                System.out.println("REQUEST");
                //A block is uploaded by a client when the client is not choking a peer,
                //and that peer is interested in the client
                if (sender.isChoked() || !sender.isInteresting()) {
                    System.out.println("Rejected request because " + sender + " is choked or not interesting");
                    return;
                }
                handleRequest(sender, msg);
            }
            case MessagesTypes.PIECE -> {
                System.out.println("PIECE");
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

    private void handleRequest(Peer from, Message msg) {
        byte[] payload = msg.getPayload();
        if (payload != null) {
            if (payload.length != 12) {
                return;
            }
            int idx = Util.convertToInt(Arrays.copyOfRange(payload, 0, 4));
            int begin = Util.convertToInt(Arrays.copyOfRange(payload, 4, 8));
            int len = Util.convertToInt(Arrays.copyOfRange(payload, 8, payload.length));
            System.out.println("REQUESTED idx: " + idx + ", begin: " + begin + ", len: " + len);

            DU.addTask(new Task(TaskType.SEND, idx, begin, len, from));
        }
    }

    private void handlePiece(Peer from, Peer to, Message msg) {
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
            to.setBlock(pieceIdx, blockIdx);
            if (to.isPieceFull(pieceIdx)) {
                to.setPiece(pieceIdx, true);
                if (to.isLastPiece(pieceIdx)) {
                    DU.addTask(new Task(TaskType.CHECK_HASH, pieceIdx, piecesInfo.getLastPieceLen()));
                } else {
                    DU.addTask(new Task(TaskType.CHECK_HASH, pieceIdx, piecesInfo.getPieceLen()));
                }
            }
            if (to.isHasAllPieces()) {
                return;
            }

            Message request = to.createRequest(from);
            if (request == null) {
                return;
            }
            addMsgToQueue(from, request);
        }
    }


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
        //вот тут что-то с id придумать надо бы
        peer.setId(peerId);
        readyMessages.add(new Handshake(infoHash, peerId));
        return true;
    }

    public void readFrom(Peer peer) {
        boolean able = readFromChannel(peer.getChannel());
        if (!able) {
            //todo это что-то значит?
            return;
        }
        while (hasFullMessage(readBytes)) {
            addFullMessages(readBytes, readyMessages);
        }
    }

    //как-то хэндлить дисконект пиров
    private boolean readFromChannel(SocketChannel channel) {
        int bytesToAllocate = piecesInfo.getBlockLen();
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read = -1;
        try {
            read = channel.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (read == -1) {
            return false;
        }
        for (int i = 0; i < read; ++i) {
            readBytes.add(buffer.get(i));
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
            length[i] = readBytes.get(i);
        }
        int messageLen = Util.convertToInt(length);
        return bytesList.size() - prefixLen >= messageLen;
    }

    private void addFullMessages(List<Byte> bytesList, Queue<Message> messagesQueue) {
        int prefixLen = 4;
        byte[] length = new byte[prefixLen];
        byte[] id = new byte[1];
        for (int i = 0; i < prefixLen; ++i) {
            length[i] = bytesList.get(i);
        }
        bytesList.subList(0, prefixLen).clear();
        id[0] = bytesList.get(0);
        bytesList.remove(0);
        System.out.println("id: " + id[0]);
        int messageLen = Util.convertToInt(length);
        int idInt = id[0];
        int payloadLen = messageLen - 1;
        if (payloadLen > 0) {
            byte[] payload = new byte[payloadLen];
            for (int i = 0; i < payloadLen; ++i) {
                payload[i] = bytesList.get(i);
            }
            bytesList.subList(0, payloadLen).clear();
            messagesQueue.add(new ProtocolMessage(idInt, payload));
            return;
        }
        messagesQueue.add(new ProtocolMessage(idInt));
    }

    public Message getReadyMsg() {
        return readyMessages.poll();
    }


    private final Map<Peer, Queue<Message>> messagesToPeer = new HashMap<>();
    private final List<Byte> readBytes = new ArrayList<>();
    private final Queue<Message> readyMessages = new ArrayDeque<>();
    private final PiecesAndBlocksInfo piecesInfo;
    private final byte[] infoHash;
    private final boolean seeder;
    private final DownloadUploadManager DU;
}
