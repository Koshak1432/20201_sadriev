package kosh.torrent;

abstract public class Message {
    abstract public byte[] createMessage();

    private int id;
}
