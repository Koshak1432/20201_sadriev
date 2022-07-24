package kosh.torrent;

public class Block {
    public Block(int offset, byte[] data) {
        this.data = data;
        size = data.length;
        idx = offset / size;
    }

    public int getIdx() {
        return idx;
    }

    public byte[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }
    private final int size;

    private final int idx;
    private final byte[] data;
}
