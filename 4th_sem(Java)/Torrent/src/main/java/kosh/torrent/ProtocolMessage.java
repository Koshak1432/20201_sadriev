package kosh.torrent;

public class ProtocolMessage extends Message {
    //<length prefix><message ID><payload>
    public ProtocolMessage(int type) {
        super(type);
        this.type = type;
    }

    public ProtocolMessage(int type, byte[] payload) {
        super(type);
        this.type = type;

    }

    //for messages without payload
    public void setInfo(int type) {
        if (type == MessagesTypes.KEEP_ALIVE) {
            len = new byte[] {0, 0, 0, 0};
            return;
        }
        len = new byte[] {0, 0, 0, 1};
        id[0] = (byte) type;
    }

    public void setInfo(int type, byte[] payload) {
        this.payload = payload;
        id[0] = (byte) type;
        switch (type) {
            case MessagesTypes.HAVE -> len = new byte[] {0, 0, 0, 5};
            case MessagesTypes.BITFIELD -> len = Util.convertToByteArr(1 + payload.length);
            case MessagesTypes.REQUEST, MessagesTypes.CANCEL -> len = new byte[] {0, 0, 0, 13};
            case MessagesTypes.PIECE -> len = ????
                    //piece: <len=0009+X><id=7><index><begin><block>, where x is the length of the block
        }
    }

    @Override
    public byte[] createMessage() {
        return new byte[0];
    }

    private byte[] len; //msg with len == 0 is keep-alive
    private byte[] id = new byte[1];
    private byte[] payload;
    private final int type;


}
