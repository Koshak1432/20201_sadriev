package kosh.torrent;

/*
* Interface which describes methods of class working with file system
 */
public interface IDownloadUploadManager {
    void addTask(Task task);

    IMessage getOutgoingMsg(Peer peer);

    Integer getSuccessfulCheck();

    Integer getUnsuccessfulCheck();

}
