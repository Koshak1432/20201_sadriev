package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;

/*
* Class represents a message sender
 */
public class MessagesSender implements IMessagesSender {

    public MessagesSender() {}

    @Override
    public boolean sendMsg(Peer peer, IMessage msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getMessage());
        try {
            peer.getChannel().write(buffer);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
