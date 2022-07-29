package kosh.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) {
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
        try {
            Seeder seeder = new Seeder("localhost", 2020);
            seeder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}