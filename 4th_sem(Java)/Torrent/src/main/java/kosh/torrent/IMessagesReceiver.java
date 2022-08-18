package kosh.torrent;

public interface IMessagesReceiver {
    boolean readFrom(Peer peer);

    boolean readHS(Peer peer);

    void addMsgToQueue(Peer peer, IMessage msg);

    void handleMsg(Peer from, Peer to, IMessage msg);

    IMessage getMsgTo(Peer peer);

    IMessage getMsgFrom(Peer peer);
}
