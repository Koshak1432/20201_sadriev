package kosh.torrent;

/*
* Interface which describes BitTorrent protocol messages sender
 */
public interface IMessagesSender {
    boolean sendMsg(Peer peer, IMessage msg);
}
