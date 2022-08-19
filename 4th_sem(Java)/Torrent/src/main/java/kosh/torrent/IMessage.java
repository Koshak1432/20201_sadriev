package kosh.torrent;


/*
* Interface which describes BitTorrent message
 */
public interface IMessage {
    byte[] getMessage();

    int getType();

    byte[] getPayload();
}
