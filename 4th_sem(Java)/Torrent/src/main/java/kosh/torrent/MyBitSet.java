package kosh.torrent;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyBitSet {
    public MyBitSet(PiecesAndBlocksInfo info, boolean seeder) {
        this.info = info;
        piecesHas = new BitSet(info.getPiecesNum());
        requestedPieces = new BitSet(info.getPiecesNum());
        requestedBlocks = new BitSet((info.getPiecesNum() - 1) * info.getBlocksInPiece() + info.getBlocksInLastPiece());

        piecesHas.set(0, info.getPiecesNum(), seeder);
        initHasMap();
    }

    private void initHasMap() {
        int blocksInPiece = info.getBlocksInPiece();
        for (int i = 0; i < info.getPiecesNum(); ++i) {
            if (isLastPiece(i)) {
                blocksInPiece = info.getBlocksInLastPiece();

            }
            boolean pieceAvailable = piecesHas.get(i);
            BitSet blocks = new BitSet(blocksInPiece);
            blocks.set(0, blocksInPiece, pieceAvailable);
            hasMap.put(i, blocks);
        }
    }

    public BitSet getPiecesHas() {
        return piecesHas;
    }

    public boolean isHasAllPieces() {
        return piecesHas.cardinality() == info.getPiecesNum();
    }

    public boolean isPieceFull(int pieceIdx) {
        int cardinality = hasMap.get(pieceIdx).cardinality();
        if (isLastPiece(pieceIdx)) {
            return cardinality == info.getBlocksInLastPiece();
        }
        return cardinality == info.getBlocksInPiece();
    }

    public boolean isLastBlock(int pieceIdx, int blockIdx) {
        return blockIdx == info.getBlocksInLastPiece() - 1 && isLastPiece(pieceIdx);
    }

    public void setPiecesHas(byte[] bitfield) {
        piecesHas = BitSet.valueOf(bitfield);
        initHasMap();
    }

    public void setPiece(int idx, boolean has) {
        piecesHas.set(idx, has);
        int numPieces = (isLastPiece(idx)) ? info.getBlocksInLastPiece() : info.getBlocksInPiece();
        hasMap.get(idx).set(0, numPieces, true);
    }

    public void setBlock(int pieceIdx, int blockIdx) {
        hasMap.get(pieceIdx).set(blockIdx);
    }

    public void setRequested(int blockIdx) {
        requestedBlocks.set(blockIdx);
    }

    public boolean isLastPiece(int idx) {
        return idx == info.getPiecesNum() - 1;
    }

    public void clearPiece(int idx) {
        int blocksInThisPiece = isLastPiece(idx) ? info.getBlocksInLastPiece() : info.getBlocksInPiece();
        setPiece(idx, false);
        hasMap.get(idx).set(0, blocksInThisPiece, false);
        requestedBlocks.set(idx * info.getBlocksInPiece(), idx * info.getBlocksInPiece() + blocksInThisPiece, false);
    }

    //завести битсет запрошенных кусков, крутиться по ним, и если не полный кусок, то грузить с него, если кончились, то выбрать новый
    public int chooseClearPiece(BitSet receiverHas) {
        int pieceIdx;
        for (pieceIdx = requestedPieces.nextSetBit(0); pieceIdx >= 0; pieceIdx = requestedPieces.nextSetBit(pieceIdx + 1)) {
            if (pieceIdx == Integer.MAX_VALUE) {
                break;
            }
            if (!isPieceFull(pieceIdx)) {
                System.out.println("piece in requested true?: " + requestedPieces.get(pieceIdx));
                System.out.println("piece isn't full, return " + pieceIdx);
                return pieceIdx;
            }
        }

        BitSet piecesToRequest = (BitSet) getPiecesHas().clone(); //I have
        piecesToRequest.flip(0, info.getPiecesNum()); //I don't have
        piecesToRequest.and(receiverHas); //I don't have and receiver has
        if (piecesToRequest.cardinality() == 0) {
            System.out.println("cardinality is zero");
            return -1;
        }
        pieceIdx = getRandomClear(piecesToRequest, info.getPiecesNum());
        requestedPieces.set(pieceIdx);
        System.out.println("CHOOSE PIECE FOR REQUEST: " + pieceIdx);
        return pieceIdx;
    }

    private int getRandomClear(BitSet piecesToRequest, int bound) {
        Random random = new Random();
        int pieceIdx = -1;
        while (pieceIdx == -1) {
            pieceIdx = piecesToRequest.nextSetBit(random.nextInt(bound));
        }
        return pieceIdx;
    }

    public int chooseClearBlock(BitSet receiverBlocks, int pieceIdx) {
        BitSet blocksToRequest = (BitSet) hasMap.get(pieceIdx).clone(); //I have
        System.out.println("i have: " + blocksToRequest + " blocks in piece " + pieceIdx);
        System.out.println("Receiver has: " + receiverBlocks);
        int blocksInThisPiece = isLastPiece(pieceIdx) ? info.getBlocksInLastPiece() : info.getBlocksInPiece();
        int fromIdx = info.getBlocksInPiece() * pieceIdx;
        blocksToRequest.or(requestedBlocks.get(fromIdx, fromIdx + blocksInThisPiece)); // I have and requested
        System.out.println("i have and requested: " + blocksToRequest + " blocks in piece " + pieceIdx);
        blocksToRequest.flip(0, blocksInThisPiece); //I don't have and not requested
        System.out.println("i don't have and not requested: " + blocksToRequest + " blocks in piece " + pieceIdx);
        blocksToRequest.and(receiverBlocks); // I don't have, not requested and receiver has
        System.out.println("i don't have, not requested and receiver has: " + blocksToRequest + " blocks in piece " + pieceIdx);
        if (blocksToRequest.cardinality() == 0) {
            return -1;
        }

        getRandomClear(blocksToRequest, blocksInThisPiece);
        return blocksToRequest.nextSetBit(0);
    }

    public BitSet getBlocksInPiece(int pieceIdx) {
        return hasMap.get(pieceIdx);
    }

    private final PiecesAndBlocksInfo info;
    private BitSet piecesHas;
    private final BitSet requestedBlocks;

    private final BitSet requestedPieces;
    private final Map<Integer, BitSet> hasMap = new HashMap<>();
}
