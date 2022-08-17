package kosh.torrent;

//<length prefix><message ID><payload>
public class ProtocolMessage implements Message {
    public ProtocolMessage(int type) {
        this.type = type;
        setInfo(type);
    }

    public ProtocolMessage(int type, byte[] payload) {
        this.type = type;
        setInfo(type, payload);
    }

    private void setInfo(int type) {
        len = (type == MessagesTypes.KEEP_ALIVE) ? Util.convertToByteArr(0) : Util.convertToByteArr(1);
        id[0] = (byte) type;
    }

    private void setInfo(int type, byte[] payload) {
        this.payload = payload;
        id[0] = (byte) type;
        switch (type) {
            case MessagesTypes.HAVE -> len = Util.convertToByteArr(5);
            case MessagesTypes.BITFIELD, MessagesTypes.PIECE -> len = Util.convertToByteArr(1 + payload.length);
            case MessagesTypes.REQUEST, MessagesTypes.CANCEL -> len = Util.convertToByteArr(13);
        }
    }

    @Override
    public byte[] getMessage() {
        if (type > MessagesTypes.NOT_INTERESTED && payload != null) {
            return Util.concatByteArrays(Util.concatByteArrays(len, id), payload);
        }
        if (type >= MessagesTypes.CHOKE) {
            return Util.concatByteArrays(len, id);
        }
        return len;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    private byte[] len; //msg with len == 0 is keep-alive
    private final byte[] id = new byte[1];
    private byte[] payload = null;
    private final int type;
}
