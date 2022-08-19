package kosh.torrent;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
* Class represents a peer
 */
public class Peer {
    public Peer(SocketChannel channel, PiecesAndBlocksInfo info) {
        this.id = generateId();
        this.channel = channel;
        this.info = info;
        this.bitset = new MyBitSet(info, false);
    }

    public Peer(SocketChannel channel, PiecesAndBlocksInfo info, boolean seeder) {
        this.id = generateId();
        this.channel = channel;
        this.info = info;
        this.bitset = new MyBitSet(info, seeder);
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

    public MyBitSet getBitset() {
        return bitset;
    }

    private int choosePieceToRequest(Peer receiver) {
        return bitset.chooseClearPiece(receiver.getBitset().getPiecesHas());
    }

    private int chooseBlockToRequest(Peer receiver, int pieceIdx) {
        return bitset.chooseClearBlock(receiver.getBitset().getBlocksInPiece(pieceIdx), pieceIdx);
    }

    /*
    * Creates REQUEST message
    * @param to to whom
    * @return IMessage request or null if couldn't choose piece or block to request
     */
    public IMessage createRequest(Peer to) {
        int pieceIdx = choosePieceToRequest(to);
        if (pieceIdx == -1) {
            return null;
        }
        int blockIdx = chooseBlockToRequest(to, pieceIdx);
        if (blockIdx == -1) {
            return null;
        }
        bitset.setRequested(info.getBlocksInPiece() * pieceIdx + blockIdx);

        int len = bitset.isLastBlock(pieceIdx, blockIdx) ? info.getLastBlockLen() : info.getBlockLen();
        byte[] begin = Util.convertToByteArr(info.getBlockLen() * blockIdx);
        byte[] lenA = Util.convertToByteArr(len);
        return new ProtocolMessage(MessagesTypes.REQUEST,
                                  Util.concatByteArrays(Util.concatByteArrays(Util.convertToByteArr(pieceIdx), begin), lenA));
    }

    public void closeConnection() {
        try {
            channel.socket().close();
            channel.close();
        } catch (IOException e) {
            System.err.println("Couldn't properly close connection with " + channel);
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        try {
            return channel.getRemoteAddress().toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
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

    private final MyBitSet bitset;
    private final List<Peer> handshaked = new ArrayList<>();
    private final PiecesAndBlocksInfo info;
    private final SocketChannel channel;
    private byte[] id;
    private boolean choked = true; //this client is choking the peer
    private boolean choking = true; //peer is choking this client, выставлять, когда отправляю пиру choke
    private boolean interested = false; //this client is interested in the peer
    private boolean interesting = false; //peer is interested in this client, выставлять, когда отправляю interested
}
