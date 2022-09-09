package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Splitter {
    public Splitter(String filePath, int numSplit) {
        try (FileChannel channel = FileChannel.open(Path.of(filePath), StandardOpenOption.READ)) {
            for (int i = 0; i < numSplit; ++i) {
                writePart(i, filePath, channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePart(int idx, String filePath, FileChannel sourceChannel) {
        String splitFileName = filePath + idx;
        int position = (idx == 0) ? 0 : 6553600;
        try (FileChannel channel = FileChannel.open(Path.of(splitFileName), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            channel.truncate(sourceChannel.size());
            sourceChannel.position(position);
            long numBytes = (idx == 0) ? 6553600 : sourceChannel.size() - 6553600;
            ByteBuffer bb = ByteBuffer.allocate((int)numBytes);
            sourceChannel.read(bb);
            bb.clear();
            channel.write(bb, position);
            System.out.println("num bytes: " + numBytes);
            System.out.println("source size: " + sourceChannel.size());
            System.out.println("source pos: " + sourceChannel.position());
            System.out.println("part channel size: " + channel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
