package kosh.torrent;

import java.util.Queue;

public interface IMessagesReceiver {
    Queue<Message> getMessages(Peer peer);

    void readFrom(Peer peer);

    boolean readHS(Peer peer);

    void addMsgToQueue(Peer peer, Message msg);

    void handleMsg(Peer from, Peer to, Message msg);

    Message getReadyMsg();
}
