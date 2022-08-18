package kosh.torrent;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyBitSet {
    public MyBitSet(PiecesAndBlocksInfo info, boolean seeder) {
        this.info = info;
        piecesHas = new BitSet(info.getPiecesNum());
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

    public int chooseClearPiece(BitSet receiverHas) {
        BitSet piecesToRequest = (BitSet) getPiecesHas().clone(); //I have
        piecesToRequest.flip(0, info.getPiecesNum()); //I don't have
        piecesToRequest.and(receiverHas); //I don't have and receiver has
        if (piecesToRequest.cardinality() == 0) {
            return -1;
        }
        Random random = new Random();
        int pieceIdx = -1;
        while (pieceIdx == -1) {
            pieceIdx = piecesToRequest.nextSetBit(random.nextInt(info.getPiecesNum()));
        }
        return pieceIdx;
    }

    public int chooseClearBlock(BitSet receiverBlocks, int pieceIdx) {
        BitSet blocksToRequest = (BitSet) hasMap.get(pieceIdx).clone(); //I have
        int blocksInThisPiece = isLastPiece(pieceIdx) ? info.getBlocksInLastPiece() : info.getBlocksInPiece();
        int fromIdx = info.getBlocksInPiece() * pieceIdx;
        blocksToRequest.or(requestedBlocks.get(fromIdx, fromIdx + blocksInThisPiece)); // I have and requested
        blocksToRequest.flip(0, blocksInThisPiece); //I don't have and not requested
        blocksToRequest.and(receiverBlocks); // I don't have, not requested and receiver has
        if (blocksToRequest.cardinality() == 0) {
            return -1;
        }
//        Random random = new Random();
//        int blockIdx = -1;
//        while (blockIdx == -1) {
//            blockIdx = blocksToRequest.nextSetBit(random.nextInt(blocksInThisPiece));
//        }
        return blocksToRequest.nextSetBit(0);
    }

    public BitSet getBlocksInPiece(int pieceIdx) {
        return hasMap.get(pieceIdx);
    }

    private final PiecesAndBlocksInfo info;
    private BitSet piecesHas;
    private final BitSet requestedBlocks;
    private final Map<Integer, BitSet> hasMap = new HashMap<>();
}
