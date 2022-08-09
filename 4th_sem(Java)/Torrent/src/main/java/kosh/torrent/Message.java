package kosh.torrent;

abstract public class Message {
    public Message(int type) {
        this.type = type;
    }
    abstract public byte[] getMessage();

    public int getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }

    private final int type;
    private byte[] payload = null;
}
