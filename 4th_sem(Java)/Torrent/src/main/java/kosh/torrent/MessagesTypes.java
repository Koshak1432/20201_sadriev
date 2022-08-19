package kosh.torrent;

public class MessagesTypes {
    //DO NOT CHANGE THESE CONSTANTS, IT WILL AFFECT PROTOCOL MESSAGE METHODS!!!
    public static final int KEEP_ALIVE = -2;
    public static final int HANDSHAKE = -1;
    public static final int CHOKE = 0;
    public static final int UNCHOKE = 1;
    public static final int INTERESTED = 2;
    public static final int NOT_INTERESTED = 3;
    public static final int HAVE = 4;
    public static final int BITFIELD = 5;
    public static final int REQUEST = 6;
    public static final int PIECE = 7;
}
