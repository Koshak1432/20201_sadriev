package kosh.torrent;

public interface IMessage {
    byte[] getMessage();

    int getType();

    byte[] getPayload();
}
