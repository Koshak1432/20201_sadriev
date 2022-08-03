package kosh.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static Map<String, Integer> parseArgs(String[] args) {
        Map<String, Integer> peersId = new HashMap<>();
        for (String arg : args) {
            String[] peerInfo = arg.split(":");
            peersId.put(peerInfo[0], Integer.parseInt(peerInfo[1]));
        }
        return peersId;
    }
    public static void main(String[] args) {
//        ConnectionManager connectionManager = new ConnectionManager("localhost", 2020);
//        connectionManager.run();

        try (OutputStream out = new FileOutputStream("test" + ".torrent")) {
            File file = new File("D:\\20201_sadriev\\4th_sem(Java)\\Torrent\\src\\main\\resources\\the art of loving.pdf");
            if (file.exists()) {
                System.out.println("creating...");
                System.out.println(file.getName());
                System.out.println(file.getPath());
                TFileCreator creator = new TFileCreator(file);
                out.write(creator.createMetaInfoFile("localhost:5000"));
            } else {
                System.out.println("File doesn't exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Seeder seeder = new Seeder("localhost", 2020);
//            seeder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        MetainfoFile torrent = new MetainfoFile("D:\\20201_sadriev\\4th_sem(Java)\\Torrent\\test.torrent");
        System.out.println("Name: " + torrent.getName());
        System.out.println("FileLen: " + torrent.getFileLen());
        System.out.println("PieceLen: " + torrent.getPieceLen());
        System.out.println("Pieces: " + Arrays.toString(torrent.getPieces()));

        //PARSER

//        System.out.println("Before parsing");
//        for (String arg : args) {
//            System.out.println(arg);
//        }
//        System.out.println("");
//        System.out.println("After parsing map");
//        Map<String, Integer> parsed = parseArgs(args);
//        for (Map.Entry<String, Integer> entry : parsed.entrySet()) {
//            System.out.println("ip = " + entry.getKey() + ", port = " + entry.getValue());
//        }
    }
}