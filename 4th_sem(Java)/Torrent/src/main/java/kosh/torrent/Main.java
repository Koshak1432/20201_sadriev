package kosh.torrent;

public class Main {

    public static void main(String[] args) {
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
        TorrentClient client = new TorrentClient(torrent, args);
    }
}