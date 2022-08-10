package kosh.torrent;

import java.util.HashMap;
import java.util.Map;

public class TorrentClient {
    public TorrentClient(MetainfoFile metainfoFile, String[] peers) {
        DownloadUploadManager downloadUploadManager = new DownloadUploadManager(metainfoFile);
        Thread downloadThread = new Thread(downloadUploadManager);
        downloadThread.start();
        ConnectionManager cm = new ConnectionManager("localhost", 6969, metainfoFile , downloadUploadManager);
        Thread connectionThread = new Thread(cm);
        connectionThread.start();

    }

    private Map<String, Integer> parseArgs(String[] args) {
        Map<String, Integer> peersId = new HashMap<>();
        for (String arg : args) {
            String[] peerInfo = arg.split(":");
            peersId.put(peerInfo[0], Integer.parseInt(peerInfo[1]));
        }
        return peersId;
    }

}
