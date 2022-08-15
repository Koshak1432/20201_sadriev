package kosh.torrent;

import java.util.ArrayList;

public class Piece {
    public Piece( int idx, final byte[] pieceData) {
        this.idx = idx;
        splitDataIntoBlocks(pieceData);
        generateHash(pieceData);
    }

    private void splitDataIntoBlocks(final byte[] data) {
        int offsetWithinPiece;
        int offset = 0;
        int blockSize = Constants.BLOCK_LEN;
        int numberOfBlocks = (int)Math.ceil((double)data.length / Constants.BLOCK_LEN);
        int mod = data.length % Constants.BLOCK_LEN;
        int lastBlockSize = (mod != 0) ? mod : blockSize;
        for (int i = 0; i < numberOfBlocks; ++i) {
            if (i == numberOfBlocks - 1) {
                blockSize = lastBlockSize;
            }
            offsetWithinPiece = i * Constants.BLOCK_LEN;
            byte[] blockData = new byte[blockSize];
            System.arraycopy(data, offset, blockData, 0, blockSize);
            Block block = new Block(idx, offsetWithinPiece, blockData.length, blockData); //also there is a size calculated inside a block
            blocks.add(block);
            offset += blockSize;
        }
    }

    public byte[] getSHA1hash() {
        return SHA1hash;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    private void generateHash(final byte[] data) {
        SHA1hash = Util.generateHash(data);
    }
    private byte[] SHA1hash;
    private final ArrayList<Block> blocks = new ArrayList<>();
    private final int idx;
}
