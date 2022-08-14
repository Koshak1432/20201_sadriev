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
        System.out.println("Ready to send " + buffer.capacity() + " bytes: " + Arrays.toString(buffer.array()));
        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Peer> getHandshaked() {
        return handshaked;
    }

    public Queue<Message> getReadyMessages() {
        return readyMessages;
    }

    //это не должно быть здесь, перенести куда-нибудь потом
    public void constructPeerMsg() {
        System.out.println("CONSTRUCTING MSG");
        boolean able = readFromChannel(channel);
        if (!able) {
            System.out.println("NOT ABLE");
            return;
        }
        while (hasFullMessage(readBytes)) {
            System.out.println("HAS FULL");
            addFullMessages(readBytes, readyMessages);
        }
    }

    private boolean readFromChannel(SocketChannel channel) {
        int bytesToAllocate = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read = -1;
        try {
            read = channel.read(buffer);
            System.out.println("read in constucting: " + read);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (read == -1) {
            return false;
        }
        for (int i = 0; i < read; ++i) {
            readBytes.add(buffer.get(i));
        }
        System.out.println("added to read bytes " + read + " bytes");
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
        System.out.println("message len: " + messageLen);
        int idInt = id[0];
        int payloadLen = messageLen - 1;
        System.out.println("payload len: " + payloadLen);
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

//    public boolean checkHS(Message myHS) {
//        Message remotePeerHS = getRemoteHS(channel);
//        System.out.println("compare 2 handshakes");
//        System.out.println(remotePeerHS);
//        System.out.println(myHS);
//        return Arrays.equals(remotePeerHS.getMessage(), myHS.getMessage());
//    }

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

    public void initPiecesHas(int numPieces, boolean has) {
        piecesHas = new BitSet(numPieces);
        piecesHas.set(0, numPieces, has);
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
        int piecesNum = (int) Math.ceilDiv(fileLen,  pieceLen);
        for (int i = 0; i < piecesNum; ++i) {
            if (i == piecesNum - 1) {
                blocksInPiece = numBlocksInLastPiece;
                requestedBlocks = new BitSet(i * (int) pieceLen / Constants.BLOCK_SIZE + blocksInPiece);
            }
            boolean pieceAvailable = piecesHas.get(i);
            BitSet blocks = new BitSet(blocksInPiece);
            blocks.set(0, blocksInPiece, pieceAvailable);
            hasMap.put(i, blocks);
        }
        System.out.println("has map size:" + hasMap.size());

        for (int i = 0; i < hasMap.size(); ++i) {
            System.out.println(hasMap.get(i));
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
    private final List<Peer> handshaked = new ArrayList<>();
    private int lastBlockSize = 0;
    private final List<Byte> readBytes = new ArrayList<>();
    private final Queue<Message> readyMessages = new ArrayDeque<>();
}
