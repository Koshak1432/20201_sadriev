package kosh.torrent;

public class Task {
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

    //сделать отдельную абстракцию под инфу, а то это дичь какая-то
    private final TaskType type;
    private Peer who = null;
    private Block block = null; //если пришёл блок и его нужно сохранить
    private int pieceLen = 0;
    private int idx = 0;
}
