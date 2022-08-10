package kosh.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    public static void main(String[] args) {
//        ConnectionManager connectionManager = new ConnectionManager("localhost", 2020);
//        connectionManager.run();

//        try (OutputStream out = new FileOutputStream("test" + ".torrent")) {
//            File file = new File("D:\\20201_sadriev\\4th_sem(Java)\\Torrent\\src\\main\\resources\\the art of loving.pdf");
//            if (file.exists()) {
//                System.out.println("creating...");
//                System.out.println(file.getName());
//                System.out.println(file.getPath());
//                TFileCreator creator = new TFileCreator(file);
//                out.write(creator.createMetaInfoFile("localhost:5000"));
//            } else {
//                System.out.println("File doesn't exists");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //получать в args[0]
        MetainfoFile torrent = new MetainfoFile("D:\\20201_sadriev\\4th_sem(Java)\\Torrent\\test.torrent");
        System.out.println("Name: " + torrent.getName());
        System.out.println("FileLen: " + torrent.getFileLen());
        System.out.println("PieceLen: " + torrent.getPieceLen());
        System.out.println("Pieces: " + Arrays.toString(torrent.getPieces()));

        TorrentClient client = new TorrentClient(torrent, Arrays.copyOfRange(args, 1, args.length));
    }
}