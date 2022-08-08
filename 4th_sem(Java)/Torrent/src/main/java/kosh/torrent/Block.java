package kosh.torrent;


//todo пофиксить креатора
public class Block {
    public Block(int idx, int begin, int len, byte[] data) {
        this.idx = idx;
        this.begin = begin;
        this.len = len;
        this.data = data;
    }


    public int getIdx() {
        return idx;
    }

    public int getBegin() {
        return begin;
    }

    public int getLen() {
        return len;
    }

    public byte[] getData() {
        return data;
    }

    private final int idx;
    private final int begin;
    private final int len;
    private final byte[] data;
}
