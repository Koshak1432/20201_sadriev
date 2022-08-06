package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;

public class PeerConnection {
    public PeerConnection(SocketChannel channel) {
        this.channel = channel;

    }

    public void sendMsg(Message msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getMessage());
        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message readMsg() {
        return messagesManager.readMsg(this);
    }
    public boolean checkHS(Message myHS) {
        return messagesManager.checkHS(channel, myHS);
    }
    public void setAmChoking(boolean amChoking) {
        this.amChoking = amChoking;
    }

    public void setAmInterested(boolean amInterested) {
        this.amInterested = amInterested;
    }

    public void setPeerChoking(boolean peerChoking) {
        this.peerChoking = peerChoking;
    }

    public void setPeerInterested(boolean peerInterested) {
        this.peerInterested = peerInterested;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public boolean isAmChoking() {
        return amChoking;
    }

    public boolean isAmInterested() {
        return amInterested;
    }

    public boolean isPeerChoking() {
        return peerChoking;
    }

    public boolean isPeerInterested() {
        return peerInterested;
    }

    public void closeConnection() {
        try {
            channel.socket().close();
            channel.close();
        } catch (IOException e) {
            System.err.println("exception while closing channel");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return channel.socket().getRemoteSocketAddress().toString();
    }

    public BitSet getPeerHas() {
        return peerHas;
    }

    public void setPeerHas(byte[] bitfield) {
        boolean[] bits = Util.convertToBits(bitfield);
        for (int i = 0; i < bits.length; ++i) {
            peerHas.set(i, bits[i]);
        }
    }

    public void setPiece(int pieceIdx, boolean has) {
        peerHas.set(pieceIdx, has);
    }

    private final SocketChannel channel; //the remote peer channel
    private boolean amChoking = true; //this client is choking the peer
    private boolean amInterested = false; //this client is interested in the peer
    private boolean peerChoking = true; //peer is choking this client
    private boolean peerInterested = false; //peer is interested in this client

    private final BitSet peerHas = new BitSet();

    private final MessagesManager messagesManager = new MessagesManager();
}
