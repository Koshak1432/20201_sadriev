package kosh.torrent;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class MyBitSet {
    public MyBitSet(int fileLen, int pieceLen, int blockLen) {
        this.fileLen = fileLen;
        this.pieceLen = pieceLen;
        this.blockLen = blockLen;
        this.piecesNum = Math.ceilDiv(fileLen, pieceLen);
        this.blocksInPiece = Math.ceilDiv(pieceLen, blockLen);

        this.lastPieceLen = (fileLen % pieceLen != 0) ? (fileLen % pieceLen) : pieceLen;
        this.lastBlockLen = (lastPieceLen % blockLen != 0) ? (lastPieceLen % blockLen) : blockLen;
        this.blocksInLastPiece = Math.ceilDiv(lastPieceLen, blockLen);
    }

    private void initBlocks(long pieceLen, long fileLen) {
        int blocksInPiece = (int) pieceLen / Constants.BLOCK_LEN;
        int modPiece = (int) (fileLen % pieceLen);
        int lastPieceSize = (int) ((modPiece != 0) ? modPiece : pieceLen);
        int numBlocksInLastPiece = Math.ceilDiv(lastPieceSize, Constants.BLOCK_LEN);
        int modBlock = lastPieceSize % Constants.BLOCK_LEN;
        lastBlockSize = (modBlock != 0) ? modBlock : Constants.BLOCK_LEN;
        int piecesNum = (int) Math.ceilDiv(fileLen,  pieceLen);
        for (int i = 0; i < piecesNum; ++i) {
            if (i == piecesNum - 1) {
                blocksInPiece = numBlocksInLastPiece;
                requestedBlocks = new BitSet(i * (int) pieceLen / Constants.BLOCK_LEN + blocksInPiece);
            }
            boolean pieceAvailable = piecesHas.get(i);
            BitSet blocks = new BitSet(blocksInPiece);
            blocks.set(0, blocksInPiece, pieceAvailable);
            hasMap.put(i, blocks);
        }
        for (int i = 0; i < hasMap.size(); ++i) {
            System.out.println(hasMap.get(i));
        }
    }


    private BitSet piecesHas = null;
    private BitSet requestedBlocks = null;
    private final Map<Integer, BitSet> hasMap = new HashMap<>();
    private final int fileLen;
    private final int pieceLen;
    private final int blockLen;
    private final int piecesNum;
    private final int blocksInPiece;
    private final int blocksInLastPiece;

    private final int lastBlockLen;
    private final int lastPieceLen;

}
