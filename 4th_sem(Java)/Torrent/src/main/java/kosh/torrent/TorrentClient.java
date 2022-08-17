package kosh.torrent;

import java.net.InetSocketAddress;
import java.util.*;

public class TorrentClient {
    //[0] -- leecher or seeder
    //[1] -- собственный адресс
    //брать адресса из аргументов и биндить сокетченнелы
    //как скачивать файл, если он у меня уже есть? куда?
    public TorrentClient(MetainfoFile metainfoFile, String[] args) {
        boolean seeder = args[0].equals("seeder");
        if (seeder) {
            System.out.println("Seeder");
        } else {
            System.out.println("Leecher");
        }
        List<InetSocketAddress> peers = parseArgs(Arrays.copyOfRange(args, 1, args.length)); //[0] -- iam
        DownloadUploadManager downloadUploadManager = new DownloadUploadManager(metainfoFile, seeder);
        Thread downloadThread = new Thread(downloadUploadManager);
        downloadThread.start();
        ConnectionManager cm = new ConnectionManager(metainfoFile , downloadUploadManager, peers, seeder);
        Thread connectionThread = new Thread(cm);
        connectionThread.start();
        try {
            downloadThread.join();
            connectionThread.join();
        } catch (InterruptedException e) {
            System.err.println("couldn't join threads");
            throw new RuntimeException(e);
        }
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
