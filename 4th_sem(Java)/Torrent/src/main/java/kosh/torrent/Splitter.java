package kosh.torrent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class Splitter {
    public Splitter(String fileName) {
        try (RandomAccessFile source = new RandomAccessFile(fileName, "r");
             FileChannel channel = source.getChannel()) {
            int fileParts = 2;
            System.out.println("file size: " + channel.size());
            for (int i = 0; i < fileParts; ++i) {
                writePart(i, fileName, channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePart(int idx, String fileName, FileChannel sourceChannel) {
        String splitFileName = fileName + idx;
        int position = (idx == 0) ? 0 : 6553600;
        try (RandomAccessFile file = new RandomAccessFile(splitFileName, "rw");
        FileChannel channel = file.getChannel()) {
            long numBytes = (idx == 0) ? 6553600 : sourceChannel.size() - 6553600;
            channel.truncate(sourceChannel.size());
            sourceChannel.position(position);
            channel.transferFrom(sourceChannel, position, numBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
