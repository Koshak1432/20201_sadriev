package kosh.torrent;

public interface IMessagesReceiver {
    void readFrom(Peer peer);

    boolean readHS(Peer peer);

    void addMsgToQueue(Peer peer, Message msg);

    Message pollMessage(Peer peer);

    void handleMsg(Peer from, Peer to, Message msg);

    Message getReadyMsg();
}
