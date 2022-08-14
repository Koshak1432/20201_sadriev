package kosh.torrent;

//<length prefix><message ID><payload>

public class ProtocolMessage extends Message {
    public ProtocolMessage(int type) {
        super(type);
        this.type = type;
        setInfo(type);
    }

    public ProtocolMessage(int type, byte[] payload) {
        super(type);
        this.type = type;
        setInfo(type, payload);
    }

    //for messages without payload
    public void setInfo(int type) {
        if (type == MessagesTypes.KEEP_ALIVE) {
            len = Util.convertToByteArr(0);
            return;
        }
        len = Util.convertToByteArr(1);
        id[0] = (byte) type;
    }

    public void setInfo(int type, byte[] payload) {
        this.payload = payload;
        id[0] = (byte) type;
        switch (type) {
            case MessagesTypes.HAVE -> len = Util.convertToByteArr(5);
            case MessagesTypes.BITFIELD, MessagesTypes.PIECE -> len = Util.convertToByteArr(1 + payload.length);
            case MessagesTypes.REQUEST, MessagesTypes.CANCEL -> len = Util.convertToByteArr(13);
        }
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
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

    public byte[] getPayload() {
        return payload;
    }

    private byte[] len; //msg with len == 0 is keep-alive
    private final byte[] id = new byte[1];
    private byte[] payload = null;
    private final int type;
}
