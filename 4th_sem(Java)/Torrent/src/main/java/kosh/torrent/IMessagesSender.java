package kosh.torrent;

public interface IMessagesSender {
    void sendMsg(Peer peer, IMessage msg);

}
