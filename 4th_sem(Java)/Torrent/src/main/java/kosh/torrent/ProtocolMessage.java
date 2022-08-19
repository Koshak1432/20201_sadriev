package kosh.torrent;

/*
 * Class represents all the BitTorrent protocol messages except Handshake
 * <length prefix><message ID><payload>
 */
public class ProtocolMessage implements IMessage {
    public ProtocolMessage(int type) {
        this.type = type;
        id[0] = (byte) type;
        setInfo(type);
    }

    public ProtocolMessage(int type, byte[] payload) {
        this.type = type;
        id[0] = (byte) type;
        setInfo(type, payload);
    }

    private void setInfo(int type) {
        len = (type == MessagesTypes.KEEP_ALIVE) ? Util.convertToByteArr(0) : Util.convertToByteArr(1);
    }

    private void setInfo(int type, byte[] payload) {
        this.payload = payload;
        switch (type) {
            case MessagesTypes.HAVE -> len = Util.convertToByteArr(5);
            case MessagesTypes.BITFIELD, MessagesTypes.PIECE -> len = Util.convertToByteArr(1 + payload.length);
            case MessagesTypes.REQUEST -> len = Util.convertToByteArr(13);
        }
    }

    @Override
    public byte[] getMessage() {
        byte[] result;
        switch (type) {
            case MessagesTypes.CHOKE, MessagesTypes.UNCHOKE,
                 MessagesTypes.INTERESTED, MessagesTypes.NOT_INTERESTED -> result = Util.concatByteArrays(len, id);
            case MessagesTypes.HAVE, MessagesTypes.BITFIELD,
                 MessagesTypes.REQUEST, MessagesTypes.PIECE -> {
                if (payload != null) {
                    result = Util.concatByteArrays(Util.concatByteArrays(len, id), payload);
                } else {
                    result = len;
                }
            }
            default -> result = len;
        }
        return result;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    private byte[] len;
    private final byte[] id = new byte[1];
    private byte[] payload = null;
    private final int type;
}
