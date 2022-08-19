package kosh.torrent;


/*
* Class represents a task for class working with file system
 */
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

    /*
    * Creates an extract block task
    * @param idx piece idx
    * @param begin offset within the piece
    * @param len requested length
    * @param sender who is requesting
    * @return new extract block task
     */
    public static Task createExtractTask(int idx, int begin, int len, Peer sender) {
        return new Task(TaskType.EXTRACT_BLOCK, idx, begin, len, sender);
    }

    /*
     * Creates a save block task
     * @param idx piece idx
     * @param begin offset within the piece
     * @param data data to save
     * @return new save task
     */
    public static Task createSaveTask(int idx, int begin, byte[] data) {
        return new Task(TaskType.SAVE, idx, begin, data);
    }

    /*
     * Creates a check hash task
     * @param idx piece idx to check
     * @param pieceLen length of the piece to check
     * @return new check hash task
     */
    public static Task createCheckTask(int idx, int pieceLen) {
        return new Task(TaskType.CHECK_HASH, idx, pieceLen);
    }

    public static Task createStopTask() {
        return new Task(TaskType.STOP);
    }

    @Override
    public String toString() {
        return type.toString();
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
