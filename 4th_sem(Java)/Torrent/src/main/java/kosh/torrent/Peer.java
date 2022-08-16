package kosh.torrent;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

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

    public List<Peer> getHandshaked() {
        return handshaked;
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

    private final MyBitSet bitset;
    private final List<Peer> handshaked = new ArrayList<>();
    private final PiecesAndBlocksInfo info;
}
