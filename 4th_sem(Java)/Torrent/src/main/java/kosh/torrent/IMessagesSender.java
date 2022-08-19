package kosh.torrent;

/*
* Interface which describes BitTorrent protocol messages sender
 */
public interface IMessagesSender {
    void sendMsg(Peer peer, IMessage msg);
}
