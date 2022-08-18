package kosh.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Main {

    public static void main(String[] args) {
        try (OutputStream out = new FileOutputStream("test" + ".torrent")) {
            File file = new File(originalFilePath + "Designing_Interfaces_Patterns_for_Effective_Interaction_Design_2020.pdf");
            if (file.exists()) {
                TFileCreator creator = new TFileCreator(file);
                out.write(creator.createMetaInfoFile("localhost:5000"));
            } else {
                System.out.println("File doesn't exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //получать в args[0]
        MetainfoFile torrent = new MetainfoFile(metaInfoFilePath + "test.torrent");
        TorrentClient client = new TorrentClient(torrent, args);
    }

    private static String originalFilePath = "D:\\20201_sadriev\\4th_sem(Java)\\Torrent\\src\\main\\resources\\";
    private static String metaInfoFilePath = "D:\\20201_sadriev\\4th_sem(Java)\\Torrent\\";
}