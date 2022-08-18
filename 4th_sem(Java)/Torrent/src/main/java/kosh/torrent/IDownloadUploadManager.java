package kosh.torrent;

public interface IDownloadUploadManager {
    void addTask(Task task);

    IMessage getOutgoingMsg(Peer peer);

    Integer getSuccessfulCheck();
    Integer getUnsuccessfulCheck();

}
