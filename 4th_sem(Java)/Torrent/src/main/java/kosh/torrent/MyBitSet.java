package kosh.torrent;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class MyBitSet {
    public MyBitSet(PiecesAndBlocksInfo info) {
        this.info = info;
        //todo do not forget about initialization of leecher and seeder
        //where to init?
        piecesHas = new BitSet(info.getPiecesNum());
        requestedBlocks = new BitSet((info.getPiecesNum() - 1) * info.getBlocksInPiece() + info.getBlocksInLastPiece());

        initHasMap();
    }

    private void initHasMap() {
        int blocksInPiece = info.getBlocksInPiece();
        for (int i = 0; i < info.getPiecesNum(); ++i) {
            if (i == info.getPiecesNum() - 1) {
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

    public void setPiecesHas(byte[] bitfield) {
        piecesHas = BitSet.valueOf(bitfield);
    }

    public void setPiece(int idx, boolean has) {
        piecesHas.set(idx, has);
    }

    private boolean isLastPiece(int idx) {
        return idx == info.getPiecesNum() - 1;
    }

    public boolean clearPiece(int idx) {
        if (idx > hasMap.size() - 1 || idx < 0) {
            return false;
        }
        int blocksInThisPiece = isLastPiece(idx) ? info.getBlocksInLastPiece() : info.getBlocksInPiece();
        setPiece(idx, false);
        hasMap.get(idx).set(0, blocksInThisPiece, false);
        requestedBlocks.set(idx * info.getBlocksInPiece(), idx * info.getBlocksInPiece() + blocksInThisPiece);
        return true;
    }

//    private void initPiecesHas(int numPieces, boolean has) {
//        piecesHas = new BitSet(numPieces);
//        piecesHas.set(0, numPieces, has);
//    }


    private final PiecesAndBlocksInfo info;
    private BitSet piecesHas = null;
    private BitSet requestedBlocks = null;
    private final Map<Integer, BitSet> hasMap = new HashMap<>();
}
