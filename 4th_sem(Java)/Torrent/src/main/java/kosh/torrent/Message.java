package kosh.torrent;

abstract public class Message {
    public Message(int type) {
        this.type = type;
    }
    abstract public byte[] createMessage();

    public int getType() {
        return type;
    }

    private final int type;
}
