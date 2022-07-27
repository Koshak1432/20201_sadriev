package kosh.torrent;

import java.util.ArrayList;

public class Piece {
    public Piece(final byte[] pieceData) {
        splitDataIntoBlocks(pieceData);
        generateHash(pieceData);
    }

    private void splitDataIntoBlocks(final byte[] data) {
        int offset = 0;
        int numberOfBlocks = (int)Math.ceil((double)data.length / Constants.blockSize);
        for (int i = 0; i < numberOfBlocks; ++i) {
            int blockSize = ((i == numberOfBlocks - 1) && data.length % Constants.blockSize != 0) ? data.length % Constants.blockSize : Constants.blockSize;
            byte[] blockData = new byte[blockSize];
            System.arraycopy(data, offset, blockData, 0, blockSize);
            Block block = new Block(offset, blockData); //also there is a size calculated inside a block
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
}
