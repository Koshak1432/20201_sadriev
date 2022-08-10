package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

//client
public class Peer {
    public Peer(SocketChannel channel) {
        this.id = Util.generateId();
        this.channel = channel;
    }

    public void sendMsg(Message msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getMessage());
        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //это не должно быть здесь, перенести куда-нибудь потом
    public Message constructPeerMsg(SocketChannel channel) {
        int bytesToAllocate = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read = -1;
        try {
            read = channel.read(buffer);
            if (read == -1) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] length = new byte[4];
        byte[] id = new byte[1];
        byte[] payload = new byte[read - id.length - length.length];
        int len = Util.convertToInt(length);
        int idInt;
        buffer.get(0, length, 0, length.length);
        if (read > length.length) {
            buffer.get(length.length, id, 0, id.length);
            buffer.get(length.length + id.length, payload, 0, payload.length);
            idInt = Util.convertToInt(id);
        } else {
            idInt = MessagesTypes.KEEP_ALIVE;
        }
        return (payload.length > 0) ? new ProtocolMessage(idInt, payload) :
                new ProtocolMessage(idInt);
    }

    private Message getRemoteHS(SocketChannel remoteChannel) {
        int infoHashIdx = 28;
        int peerIdIdx = infoHashIdx + 20;
        //прочитать и сохранить все данные?
        byte[] infoHash = new byte[20];
        byte[] peerId = new byte[20];
        ByteBuffer byteBuffer = ByteBuffer.allocate(68);
        try {
            remoteChannel.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuffer.get(infoHashIdx, infoHash, 0, infoHash.length);
        byteBuffer.get(peerIdIdx, peerId, 0, peerId.length);
        //вот тут что-то с id придумать надо бы
        setId(peerId);
        return new Handshake(infoHash, peerId);
    }

    public boolean checkHS(Message myHS) {
        Message remotePeerHS = getRemoteHS(channel);
        return Arrays.equals(remotePeerHS.getMessage(), myHS.getMessage());
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

    public BitSet getPiecesHas() {
        return piecesHas;
    }

    public void setPiecesHas(byte[] bitfield) {
        piecesHas = BitSet.valueOf(bitfield);
    }

    public Map<Integer, BitSet> getHasMap() {
        return hasMap;
    }

    public BitSet getRequestedBlocks() {
        return requestedBlocks;
    }

    public void setPiece(int pieceIdx, boolean has) {
        this.piecesHas.set(pieceIdx, has);
    }


    public int getIdxLastRequested() {
        return idxLastRequested;
    }

    public void setIdxLastRequested(int idxLastRequested) {
        this.idxLastRequested = idxLastRequested;
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

    public void initBlocks(long pieceLen, long fileLen) {
        int blocksInPiece = (int) pieceLen / Constants.BLOCK_SIZE;
        int modPiece = (int) (fileLen % pieceLen);
        int lastPieceSize = (int) ((modPiece != 0) ? modPiece : pieceLen);
        int numBlocksInLastPiece = lastPieceSize / Constants.BLOCK_SIZE;
        int modBlock = lastPieceSize % Constants.BLOCK_SIZE;
        lastBlockSize = (modBlock != 0) ? modBlock : Constants.BLOCK_SIZE;
        for (int i = 0; i < piecesHas.length(); ++i) {
            if (i == piecesHas.length() - 1) {
                blocksInPiece = numBlocksInLastPiece;
                requestedBlocks = new BitSet(i * (int) pieceLen / Constants.BLOCK_SIZE + blocksInPiece);
            }
            boolean pieceAvailable = piecesHas.get(i);
            BitSet blocks = new BitSet(blocksInPiece);
            blocks.set(0, blocksInPiece, pieceAvailable);
            hasMap.put(i, blocks);
        }
    }

    public int getLastBlockSize() {
        return lastBlockSize;
    }

    private byte[] id;
    private final SocketChannel channel;
    private int idxLastRequested = 0;
    private boolean choked = true; //this client is choking the peer
    private boolean choking = true; //peer is choking this client, выставлять, когда отправляю пиру choke
    private boolean interested = false; //this client is interested in the peer
    private boolean interesting = false; //peer is interested in this client, выставлять, когда отправляю interested

    private BitSet piecesHas;
    private BitSet requestedBlocks;
    //ключ -- номер куска, значение -- битсет длиной pieceSize / blockSize
    private final Map<Integer, BitSet> hasMap = new HashMap<>();
    private int lastBlockSize = 0;
}
