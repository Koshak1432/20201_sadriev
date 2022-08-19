package kosh.torrent;


/*
 * Class represents helpful information about pieces and blocks
 */
public class PiecesAndBlocksInfo {
    public PiecesAndBlocksInfo(int fileLen, int pieceLen, int blockLen) {
        this.pieceLen = pieceLen;
        this.blockLen = blockLen;
        this.piecesNum = Math.ceilDiv(fileLen, pieceLen);
        this.blocksInPiece = Math.ceilDiv(pieceLen, blockLen);

        this.lastPieceLen = (fileLen % pieceLen != 0) ? (fileLen % pieceLen) : pieceLen;
        this.lastBlockLen = (lastPieceLen % blockLen != 0) ? (lastPieceLen % blockLen) : blockLen;
        this.blocksInLastPiece = Math.ceilDiv(lastPieceLen, blockLen);
    }

    public int getPieceLen() {
        return pieceLen;
    }

    public int getBlockLen() {
        return blockLen;
    }

    public int getPiecesNum() {
        return piecesNum;
    }

    public int getBlocksInPiece() {
        return blocksInPiece;
    }

    public int getBlocksInLastPiece() {
        return blocksInLastPiece;
    }

    public int getLastBlockLen() {
        return lastBlockLen;
    }

    public int getLastPieceLen() {
        return lastPieceLen;
    }

    private final int pieceLen;
    private final int blockLen;
    private final int piecesNum;
    private final int blocksInPiece;
    private final int blocksInLastPiece;

    private final int lastBlockLen;
    private final int lastPieceLen;
}
