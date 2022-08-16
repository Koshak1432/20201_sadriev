package kosh.torrent;

public class Task {
    public Task(TaskType type) {
        this.type = type;
    }
    //request
    public Task(TaskType type, int idx, int begin, int len, Peer sender) {
        this.type = type;
        this.who = sender;
        this.block = new Block(idx, begin, len, null);
    }

    //piece
    public Task(TaskType type, int idx, int begin, byte[] blockData) {
        this.type = type;
        this.block = new Block(idx, begin, blockData.length, blockData);
    }

    public Task(TaskType type, int idx, int pieceLen) {
        this.type = type;
        this.idx = idx;
        this.pieceLen = pieceLen;
    }

    public TaskType getType() {
        return type;
    }

    public Block getBlock() {
        return block;
    }

    public Peer getWho() {
        return who;
    }

    public int getPieceLen() {
        return pieceLen;
    }

    public int getIdx() {
        return idx;
    }

    private final TaskType type;
    private Peer who = null;
    private Block block = null;
    private int pieceLen = 0;
    private int idx = 0;
}
