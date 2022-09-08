package kosh.torrent;

import java.net.InetSocketAddress;
import java.util.*;

/*
 * Class represents torrent client
 */
public class TorrentClient {
    /*
     * @param metaInfoFile the .torrent file
     * @param args args from command line, [0] -- leecher/seeder, [1] -- id, [2] -- own address
     */
    public TorrentClient(MetainfoFile metainfoFile, String[] args) {
        boolean seeder = args[0].equals("seeder");
        System.out.println(args[0]);
        List<InetSocketAddress> peers = parseArgs(Arrays.copyOfRange(args, 2, args.length)); //[0] -- iam
        for (InetSocketAddress add : peers) {
            System.out.println(add);
        }
        System.out.println(args[1]);
        DownloadUploadManager DU = new DownloadUploadManager(metainfoFile, seeder, Integer.parseInt(args[1]));
        Thread downloadThread = new Thread(DU);
        downloadThread.start();
        ConnectionManager cm = new ConnectionManager(metainfoFile , DU, peers, seeder, Integer.parseInt(args[1]));
        Thread connectionThread = new Thread(cm);
        connectionThread.start();
        try {
            downloadThread.join();
            connectionThread.join();
        } catch (InterruptedException e) {
            if (!downloadThread.isInterrupted()) {
                downloadThread.interrupt();
            }
            if (!connectionThread.isInterrupted()) {
                connectionThread.interrupt();
            }
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
