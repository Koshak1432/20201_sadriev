package kosh.torrent;

import java.io.IOException;
import java.util.BitSet;

//client
public class Peer {
    public Peer(String hostname, int port) throws IOException {
        this.ip = hostname;
        this.port = port;
        this.id = Util.generateId(); //maybe move it here
    }

    public byte[] getId() {
        return id;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getUploaded() {
        return uploaded;
    }

    public BitSet getPieces() {
        return pieces;
    }

    public boolean isInterested() {
        return interested;
    }

    public boolean isInteresting() {
        return interesting;
    }

    public boolean isChoked() {
        return choked;
    }

    public boolean isChoking() {
        return choking;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public void setInteresting(boolean interesting) {
        this.interesting = interesting;
    }

    public void setChoked(boolean choked) {
        this.choked = choked;
    }

    public void setChoking(boolean choking) {
        this.choking = choking;
    }

    public void setPieces(byte[] bitfield) {
        boolean[] bits = Util.convertToBits(bitfield);
        for (int i = 0; i < bits.length; ++i) {
            pieces.set(i, bits[i]);
        }
    }

    public void setPiece(int pieceIdx, boolean has) {
        pieces.set(pieceIdx, has);
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
    private final byte[] id;
    private boolean interested = false;
    private boolean interesting = false;
    private boolean choked = true;
    private boolean choking = true;
    private final String ip;
    private final int port;
    private int downloaded = 0;
    private int uploaded = 0;

    private final BitSet pieces = new BitSet();

}
