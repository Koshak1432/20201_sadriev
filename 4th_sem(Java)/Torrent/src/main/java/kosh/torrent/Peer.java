package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Peer {
    public Peer(SocketChannel channel, PiecesAndBlocksInfo info) {
        this.id = generateId();
        this.channel = channel;
        this.bitset = new MyBitSet(info);
        this.info = info;
    }

    private byte[] generateId() {
        byte[] id = new byte[20];
        Random random = new Random(System.currentTimeMillis());
        random.nextBytes(id);
        return id;
    }

    public void sendMsg(Message msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getMessage());
        try {
            channel.write(buffer);
        } catch (IOException e) {
            System.err.println("Catch an exception while writing message to " + this);
            e.printStackTrace();
        }
    }

    public List<Peer> getHandshaked() {
        return handshaked;
    }

    public Queue<Message> getReadyMessages() {
        return readyMessages;
    }

    public boolean isHasAllPieces() {
        return bitset.isHasAllPieces();
    }

    public boolean isPieceFull(int pieceIdx) {
        return bitset.isPieceFull(pieceIdx);
    }

    public boolean isLastPiece(int pieceIdx) {
        return bitset.isLastPiece(pieceIdx);
    }
    public void clearPiece(int idx) {
        bitset.clearPiece(idx);
    }

    public void setBlock(int pieceIdx, int blockIdx) {
        bitset.setBlock(pieceIdx, blockIdx);
    }

    public BitSet getPiecesHas() {
        return bitset.getPiecesHas();
    }

    public int choosePieceToRequest(Peer receiver) {
        return bitset.chooseClearPiece(receiver.getPiecesHas());
    }

    public int chooseBlockToRequest(Peer receiver, int pieceIdx) {
        return bitset.chooseClearBlock(receiver.getBlocksInPiece(pieceIdx), pieceIdx);
    }

    public Message createRequest(Peer to) {
        int pieceIdx = choosePieceToRequest(to);
        if (pieceIdx == -1) {
            return null;
        }
        int blockIdx = chooseBlockToRequest(to, pieceIdx);
        if (blockIdx == -1) {
            return null;
        }

        bitset.setRequested(info.getBlocksInPiece() * pieceIdx + blockIdx);

        int len = (isLastPiece(pieceIdx) && isPieceFull(pieceIdx)) ? info.getLastBlockLen() : info.getBlockLen();
        byte[] begin = Util.convertToByteArr(info.getBlockLen() * blockIdx);
        byte[] lenA = Util.convertToByteArr(len);
        return new ProtocolMessage(MessagesTypes.REQUEST,
                                  Util.concatByteArrays(Util.concatByteArrays(Util.convertToByteArr(pieceIdx), begin), lenA));
    }

    private BitSet getBlocksInPiece(int pieceIdx) {
        return bitset.getBlocksInPiece(pieceIdx);
    }

    //todo подумать это не должно быть здесь, перенести куда-нибудь потом???
    public void constructPeerMsg() {
        boolean able = readFromChannel(channel);
        if (!able) {
            //todo это что-то значит?
            System.out.println("NOT ABLE TO READ");
            return;
        }
        while (hasFullMessage(readBytes)) {
            addFullMessages(readBytes, readyMessages);
        }
    }

    private boolean readFromChannel(SocketChannel channel) {
        int bytesToAllocate = Constants.BLOCK_LEN / 2;
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

    public Message getRemoteHS() {
        int infoHashIdx = 28;
        int peerIdIdx = infoHashIdx + 20;
        //прочитать и сохранить все данные?
        byte[] infoHash = new byte[20];
        byte[] peerId = new byte[20];
        ByteBuffer byteBuffer = ByteBuffer.allocate(68);
        int read;
        try {
            read = channel.read(byteBuffer);
            System.out.println("read " + read + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuffer.get(infoHashIdx, infoHash, 0, infoHash.length);
        byteBuffer.get(peerIdIdx, peerId, 0, peerId.length);
        //вот тут что-то с id придумать надо бы
        setId(peerId);
        return new Handshake(infoHash, peerId);
    }

    public void closeConnection() {
        try {
            channel.socket().close();
            channel.close();
        } catch (IOException e) {
            System.err.println("exception while closing channel");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return channel.socket().getRemoteSocketAddress().toString();
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public void setPiecesHas(byte[] bitfield) {
        bitset.setPiecesHas(bitfield);
    }

    public void setPiece(int pieceIdx, boolean has) {
        bitset.setPiece(pieceIdx, has);
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public boolean isChoked() {
        return choked;
    }

    public boolean isInterested() {
        return interested;
    }

    public boolean isChoking() {
        return choking;
    }

    public boolean isInteresting() {
        return interesting;
    }

    public void setChoked(boolean choked) {
        this.choked = choked;
    }

    public void setChoking(boolean choking) {
        this.choking = choking;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public void setInteresting(boolean interesting) {
        this.interesting = interesting;
    }

    private byte[] id;
    private final SocketChannel channel;
    private boolean choked = true; //this client is choking the peer
    private boolean choking = true; //peer is choking this client, выставлять, когда отправляю пиру choke
    private boolean interested = false; //this client is interested in the peer
    private boolean interesting = false; //peer is interested in this client, выставлять, когда отправляю interested

    private MyBitSet bitset;
    private final List<Peer> handshaked = new ArrayList<>();
    private final List<Byte> readBytes = new ArrayList<>();
    private final Queue<Message> readyMessages = new ArrayDeque<>();

    private final PiecesAndBlocksInfo info;
}
