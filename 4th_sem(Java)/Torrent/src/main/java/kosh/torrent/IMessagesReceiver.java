package kosh.torrent;

public interface IMessagesReceiver {
    void readFrom(Peer peer);

    boolean readHS(Peer peer);

    void addMsgToQueue(Peer peer, Message msg);

    void handleMsg(Peer from, Peer to, Message msg);

    Message getMsgTo(Peer peer);

    Message getReadyMsg();
}
