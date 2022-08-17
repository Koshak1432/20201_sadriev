package kosh.torrent;

public interface Message {
    byte[] getMessage();

    int getType();

    byte[] getPayload();
}
