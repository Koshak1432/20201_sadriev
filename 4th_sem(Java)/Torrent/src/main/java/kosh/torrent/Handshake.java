package kosh.torrent;

/*
* A handshake message in form: <pstrlen><pstr><reserved><info_hash><peer_id>
* pstrlen: string length of <pstr>, as a single raw byte.
* pstr: string identifier of the protocol.
* reserved: 8 reserved bytes.
* info_hash: 20-byte SHA1 hash of the info key in the metainfo file.
* peer_id: 20-byte string used as a unique ID for the client.
 */
public class Handshake implements IMessage {
    public Handshake(byte[] infoHash, byte[] peerId) {
        this.infoHash = infoHash;
        this.peerId = peerId;
    }

    public byte[] getMessage() {
        return Util.concatByteArrays(pLength, Util.concatByteArrays(protocol, Util.concatByteArrays(reserved, Util.concatByteArrays(infoHash, peerId))));
    }

    @Override
    public int getType() {
        return MessagesTypes.HANDSHAKE;
    }

    @Override
    public byte[] getPayload() {
        return getMessage();
    }

    private final byte[] protocol = "BitTorrent protocol".getBytes();
    private final byte[] pLength = {19};
    private final byte[] reserved = {0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] infoHash;
    private final byte[] peerId;
}
