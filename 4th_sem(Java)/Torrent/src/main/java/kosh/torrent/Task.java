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


    public TaskType getType() {
        return type;
    }

    public Block getBlock() {
        return block;
    }

    public Peer getWho() {
        return who;
    }

    private final TaskType type;
    private Peer who = null;
    private Block block = null; //если пришёл блок и его нужно сохранить
}
