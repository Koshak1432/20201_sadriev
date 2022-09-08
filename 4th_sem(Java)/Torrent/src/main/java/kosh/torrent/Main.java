package kosh.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Main {

    public static void main(String[] args) {
//        try (OutputStream out = new FileOutputStream(metaInfoName)) {
//            File file = new File(metaInfoFilePath + "Deep_Learning.pdf");
//            if (file.exists()) {
//                TFileCreator creator = new TFileCreator(file);
//                out.write(creator.createMetaInfoFile("localhost:5000"));
//            } else {
//                System.out.println("File doesn't exists");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }

        //получать в args[0]
        MetainfoFile torrent = new MetainfoFile(metaInfoFilePath + metaInfoName);
        TorrentClient client = new TorrentClient(torrent, args);
//        Splitter splitter = new Splitter("C:\\Users\\sadri\\20201_sadriev\\4th_sem(Java)\\Torrent\\Deep_Learning.pdf");
    }

//    private static String originalFilePath = "D:\\20201_sadriev\\4th_sem(Java)\\Torrent\\src\\main\\resources\\";
    private static String metaInfoFilePath = "C:\\Users\\sadri\\20201_sadriev\\4th_sem(Java)\\Torrent\\";
    private static String metaInfoName = "test2.torrent";
}