package kosh.torrent;

import java.net.InetSocketAddress;
import java.util.*;

public class TorrentClient {
    //[0] -- leecher or seeder
    //[1] -- собственный адресс
    //брать адресса из аргументов и биндить сокетченнелы
    //как скачивать файл, если он у меня уже есть? куда?
    public TorrentClient(MetainfoFile metainfoFile, String[] args) {
        boolean leecher = args[0].equals("leecher");
        if (leecher) {
            System.out.println("Leecher");
        } else {
            System.out.println("Seeder");
        }
        List<InetSocketAddress> peers = parseArgs(Arrays.copyOfRange(args, 1, args.length)); //[0] -- iam
        DownloadUploadManager downloadUploadManager = new DownloadUploadManager(metainfoFile);
        Thread downloadThread = new Thread(downloadUploadManager);
        downloadThread.start();
        ConnectionManager cm = new ConnectionManager(metainfoFile , downloadUploadManager, peers, leecher);
        Thread connectionThread = new Thread(cm);
        connectionThread.start();

        System.out.println("After parsing map");
    }

    private List<InetSocketAddress> parseArgs(String[] args) {
        List<InetSocketAddress> addresses = new ArrayList<>();
        for (String arg : args) {
            String[] peerInfo = arg.split(":");
            InetSocketAddress address = new InetSocketAddress(peerInfo[0], Integer.parseInt(peerInfo[1]));
            addresses.add(address);
        }
        return addresses;
    }


}
