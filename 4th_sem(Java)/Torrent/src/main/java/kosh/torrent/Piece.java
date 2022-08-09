package kosh.torrent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Piece {
    public Piece( int idx, final byte[] pieceData) {
        this.idx = idx;
        splitDataIntoBlocks(pieceData);
        generateHash(pieceData);
//        debug();
    }

    private void splitDataIntoBlocks(final byte[] data) {
        int offsetWithinPiece;
        int offset = 0;
        int blockSize = Constants.BLOCK_SIZE;
        int numberOfBlocks = (int)Math.ceil((double)data.length / Constants.BLOCK_SIZE);
        int mod = data.length % Constants.BLOCK_SIZE;
        int lastBlockSize = (mod != 0) ? mod : blockSize;
        for (int i = 0; i < numberOfBlocks; ++i) {
            if (i == numberOfBlocks - 1) {
                blockSize = lastBlockSize;
            }
            offsetWithinPiece = i * Constants.BLOCK_SIZE;
            byte[] blockData = new byte[blockSize];
            System.arraycopy(data, offset, blockData, 0, blockSize);
            Block block = new Block(idx, offsetWithinPiece, blockData.length, blockData); //also there is a size calculated inside a block
            blocks.add(block);
            offset += blockSize;
        }
    }

    public void debug() {
        System.out.println("SHA1 hash: " + Arrays.toString(Arrays.toString(SHA1hash).getBytes(StandardCharsets.UTF_8)));
        System.out.println("LEN: " + SHA1hash.length);
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
