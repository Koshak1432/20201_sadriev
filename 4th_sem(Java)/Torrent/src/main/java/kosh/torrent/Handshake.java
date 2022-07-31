package kosh.torrent;

public class Handshake extends Message {
    public Handshake(byte[] infoHash, byte[] peerId) {
        super(MessagesTypes.HANDSHAKE);
        this.infoHash = infoHash;
        this.peerId = peerId;
    }

    public byte[] createMessage() {
        return Util.concatByteArrays(pLength, Util.concatByteArrays(protocol, Util.concatByteArrays(reserved, Util.concatByteArrays(infoHash, peerId))));
    }

    private final byte[] protocol = "BitTorrent protocol".getBytes();
    private final byte[] pLength = {19};
    private final byte[] reserved = {0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] infoHash;
    private final byte[] peerId;
}
