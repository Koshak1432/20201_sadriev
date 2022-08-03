package kosh.torrent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Piece {
    public Piece(final byte[] pieceData) {
        splitDataIntoBlocks(pieceData);
        generateHash(pieceData);
//        debug();
    }

    private void splitDataIntoBlocks(final byte[] data) {
        int offset = 0;
        int numberOfBlocks = (int)Math.ceil((double)data.length / Constants.BLOCK_SIZE);
        for (int i = 0; i < numberOfBlocks; ++i) {
            int blockSize = ((i == numberOfBlocks - 1) && data.length % Constants.BLOCK_SIZE != 0) ? data.length % Constants.BLOCK_SIZE : Constants.BLOCK_SIZE;
            byte[] blockData = new byte[blockSize];
            System.arraycopy(data, offset, blockData, 0, blockSize);
            Block block = new Block(offset, blockData); //also there is a size calculated inside a block
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
}
