package kosh.torrent;

abstract public class Message {
    public Message(int id) {
        this.id = id;
    }
    abstract public byte[] createMessage();

    public int getId() {
        return id;
    }

    private final int id;
}
