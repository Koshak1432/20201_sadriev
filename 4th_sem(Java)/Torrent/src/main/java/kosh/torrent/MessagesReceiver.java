package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;


/*
* Class which contains and receives BitTorrent protocol messages
 */
public class MessagesReceiver implements IMessagesReceiver {

    /*
    * @param infoHash the hash of the info in .torrent file
    * @param DU class working with file system
    * @param piecesInfo helpful info
     */
    public MessagesReceiver(byte[] infoHash, DownloadUploadManager DU, PiecesAndBlocksInfo piecesInfo) {
        this.infoHash = infoHash;
        this.DU = DU;
        this.piecesInfo = piecesInfo;
    }

    /*
    * Gets a message to peer
    * @param peer recipient
     */
    @Override
    public IMessage getMsgTo(Peer peer) {
        return messagesToPeer.get(peer).poll();
    }

    /*
    * Adds message to queue
    * @param peer recipient
    * @param msg message to add
     */
    @Override
    public void addMsgToQueue(Peer peer, IMessage msg) {
        addToMessages(messagesToPeer, peer, msg);
    }

    /*
    * Handles given message
    * @param sender from whom the message came
    * @param receiver the recipient of the message
    * @param msg the message to handle
     */
    @Override
    public void handleMsg(Peer sender, Peer receiver, IMessage msg) {
        switch (msg.getType()) {
            case MessagesTypes.KEEP_ALIVE -> System.out.println("Got KEEP ALIVE from " + sender);
            case MessagesTypes.HANDSHAKE -> {
                System.out.println("Got HANDSHAKE from " + sender);
                //pass sender's id for proper handshakes, cause don't have a tracker
                if (!Arrays.equals(msg.getMessage(), new Handshake(infoHash, sender.getId()).getMessage())) {
                    System.out.println("HS are different");
                    sender.closeConnection();
                    return;
                }
                receiver.getHandshaked().add(sender);
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.BITFIELD, receiver.getBitset().getPiecesHas().toByteArray()));
                isDownloadingFrom.put(sender, false);
            }
            case MessagesTypes.CHOKE -> {
                System.out.println("Got CHOKE from " + sender);
                sender.setChoking(true);
            }
            case MessagesTypes.UNCHOKE -> {
                System.out.println("Got UNCHOKE from " + sender);
                sender.setChoking(false);
                boolean state = createRequest(sender, receiver);
                isDownloadingFrom.put(sender, state);
            }
            case MessagesTypes.INTERESTED -> {
                System.out.println("Got INTERESTED from " + sender);
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.UNCHOKE));
                sender.setInteresting(true);
                sender.setChoked(false);
            }
            case MessagesTypes.NOT_INTERESTED -> {
                System.out.println("Got NOT INTERESTED from " + sender);
                sender.setInteresting(false);
            }
            case MessagesTypes.HAVE -> {
                System.out.println("Got HAVE(idx: " + Util.convertToInt(msg.getPayload()) + ") from " + sender);
                if (!receiver.getBitset().isHasAllPieces()) {
                    sender.getBitset().setPiece(Util.convertToInt(msg.getPayload()), true);
                }
                if (!isDownloadingFrom.get(sender)) {
                    boolean state = createRequest(sender, receiver);
                    isDownloadingFrom.put(sender, state);
                }
            }
            case MessagesTypes.BITFIELD -> {
                System.out.println("Got BITFIELD from " + sender);
                if (msg.getMessage().length > 5) {
                    sender.getBitset().setPiecesHas(msg.getPayload());
                }
                addMsgToQueue(sender, new ProtocolMessage(MessagesTypes.INTERESTED));
                sender.setInterested(true);
            }
            case MessagesTypes.REQUEST -> {
                System.out.println("Got REQUEST from " + sender);
                //A block is uploaded by a client when the client is not choking a peer,
                //and that peer is interested in the client
                if (sender.isChoked() || !sender.isInteresting()) {
                    System.out.println("Rejected request because " + sender + " is choked or not interesting");
                    return;
                }
                handleRequest(sender, msg);
            }
            case MessagesTypes.PIECE -> {
                System.out.println("Got PIECE from " + sender);
                //A block is downloaded by the client when the client is interested in a peer,
                //and that peer is not choking the client
                if (!sender.isInterested() || sender.isChoking()) {
                    System.out.println("Rejected piece because " + sender + " is choking or not interested");
                    return;
                }
                handlePiece(sender, receiver, msg);
            }
        }
    }

    private boolean createRequest(Peer to, Peer from) {
        IMessage request = from.createRequest(to);
        if (request == null) {
            System.out.println("REQUEST IS NULL(message receiver 129)");
            return false;
        }
        addMsgToQueue(to, request);
        return true;
    }

    /*
    * Reads a handshake from peer
    * @param peer from whom to read
    * @return false if error occurs, else true
     */
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

    /*
    * Reads from peer
    * @param peer from whom to read
    * @return false if can't read data, probably, peer disconnected, else true
     */
    @Override
    public boolean readFrom(Peer peer) {
        boolean able = readFromChannel(peer);
        if (!able) {
            return false;
        }
        while (hasFullMessage(getBytes(peer))) {
            addFullMessage(getBytes(peer), peer);
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

    private void addToMessages(Map<Peer, Queue<IMessage>> map, Peer peer, IMessage msg) {
        if (map.containsKey(peer)) {
            map.get(peer).add(msg);
            return;
        }
        Queue<IMessage> messages = new LinkedList<>();
        messages.add(msg);
        map.put(peer, messages);
    }

    private void addMsgFrom(Peer peer, IMessage msg) {
        addToMessages(messagesFromPeer, peer, msg);
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
            System.out.println("REQUEST idx: " + idx + ", begin: " + begin + ", len: " + len);

            DU.addTask(Task.createExtractTask(idx, begin, len, from));
        }
    }

    private void handlePiece(Peer from, Peer to, IMessage msg) {
        byte[] payload = msg.getPayload();
        if (payload != null) {
            if (payload.length < 8) {
                return;
            }
            int pieceIdx = Util.convertToInt(Arrays.copyOfRange(payload, 0, 4));
            int begin = Util.convertToInt(Arrays.copyOfRange(payload, 4, 8));
            byte[] blockData = Arrays.copyOfRange(payload, 8, payload.length);

            System.out.println("PIECE idx: " + pieceIdx + ", begin: " + begin + ", blockData len: " + blockData.length);

            DU.addTask(Task.createSaveTask(pieceIdx, begin, blockData));
            int blockIdx = begin / piecesInfo.getBlockLen();
            to.getBitset().setBlock(pieceIdx, blockIdx);
            if (to.getBitset().isPieceFull(pieceIdx)) {
                to.getBitset().setPiece(pieceIdx, true);
                int pieceLen = to.getBitset().isLastPiece(pieceIdx) ? piecesInfo.getLastPieceLen() : piecesInfo.getPieceLen();
                DU.addTask(Task.createCheckTask(pieceIdx, pieceLen));
            }
            if (to.getBitset().isHasAllPieces()) {
                return;
            }
            boolean state = createRequest(from, to);
            isDownloadingFrom.put(from, state);
        }
    }

    private List<Byte> getBytes(Peer peer) {
        if (!readBytes.containsKey(peer)) {
            readBytes.put(peer, new LinkedList<>());
        }
        return readBytes.get(peer);
    }

    /*
    * Reads portion of data from peer
    * @param peer from whom to read
    * @return false if can't read, probably, when peer disconnected, else true
     */
    private boolean readFromChannel(Peer peer) {
        int bytesToAllocate = piecesInfo.getBlockLen();
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read;
        try {
            read = peer.getChannel().read(buffer);
            if (read == -1) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        List<Byte> bytes = getBytes(peer);
        for (int i = 0; i < read; ++i) {
            bytes.add(buffer.get(i));
        }
        buffer.clear();
        return true;
    }

    /*
    * Tests whether there is a full message to construct
    * @param bytesList list with read bytes
    * @return true if there is a full message, else false
     */
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

    /*
    * Reads full message from bytes list
    * @param bytesList list with read bytes
    * @param peer from whom message came
     */
    private void addFullMessage(List<Byte> bytesList, Peer peer) {
        int prefixLen = 4;
        byte[] length = new byte[prefixLen];
        byte[] id = new byte[1];
        for (int i = 0; i < prefixLen; ++i) {
            length[i] = bytesList.get(i);
        }
        bytesList.subList(0, prefixLen).clear();
        id[0] = bytesList.get(0);
        bytesList.remove(0);
        int messageLen = Util.convertToInt(length);
        int idInt = id[0];
        int payloadLen = messageLen - 1;
        IMessage msg = new ProtocolMessage(idInt);
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
    private final PiecesAndBlocksInfo piecesInfo;
    private final byte[] infoHash;
    private final DownloadUploadManager DU;
    private final Map<Peer, Boolean> isDownloadingFrom = new HashMap<>();
}
