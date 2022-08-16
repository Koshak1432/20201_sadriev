package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MessagesSender implements IMessagesSender {

    public MessagesSender() {}

    public void sendMsg(Peer peer, Message msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getMessage());
        try {
            peer.getChannel().write(buffer);
        } catch (IOException e) {
            System.err.println("Catch an exception while writing message to " + this);
            e.printStackTrace();
        }
    }
}
