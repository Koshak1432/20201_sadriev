package kosh.torrent;

public interface IDownloadUploadManager {
    void addTask(Task task);

    Message getOutgoingMsg(Peer peer);

    Integer getSuccessfulCheck();
    Integer getUnsuccessfulCheck();

}
