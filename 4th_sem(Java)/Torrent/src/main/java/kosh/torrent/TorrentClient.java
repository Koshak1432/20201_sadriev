package kosh.torrent;

public class TorrentClient {
    public TorrentClient() {

    }

    public void download(MetainfoFile metainfoFile, String[] peers) {
        DownloadUploadManager downloadUploadManager = new DownloadUploadManager(metainfoFile);
        Thread downloadThread = new Thread(downloadUploadManager);
        downloadThread.start();
        ConnectionManager cm = new ConnectionManager("localhost", 6969, ); //где свой ХШ хранить?
    }

}
