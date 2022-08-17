package kosh.torrent;

public class Task {
    //stop
    private Task(TaskType type) {
        this.type = type;
    }

    //extractBlock to send
    private Task(TaskType type, int idx, int begin, int len, Peer sender) {
        this.type = type;
        this.who = sender;
        this.block = new Block(idx, begin, len, null);
    }

    //saveBlock
    private Task(TaskType type, int idx, int begin, byte[] blockData) {
        this.type = type;
        this.block = new Block(idx, begin, blockData.length, blockData);
    }

    //checkHash
    private Task(TaskType type, int idx, int pieceLen) {
        this.type = type;
        this.idx = idx;
        this.pieceLen = pieceLen;
    }

    public static Task createExtractTask(int idx, int begin, int len, Peer sender) {
        return new Task(TaskType.EXTRACT_BLOCK, idx, begin, len, sender);
    }

    public static Task createSaveTask(int idx, int begin, byte[] data) {
        return new Task(TaskType.SAVE, idx, begin, data);
    }

    public static Task createCheckTask(int idx, int pieceLen) {
        return new Task(TaskType.CHECK_HASH, idx, pieceLen);
    }

    public static Task createStopTask() {
        return new Task(TaskType.STOP);
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
