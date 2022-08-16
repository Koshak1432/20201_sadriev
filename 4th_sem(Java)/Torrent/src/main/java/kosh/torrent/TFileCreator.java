package kosh.torrent;

import com.dampcake.bencode.Bencode;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class TFileCreator {
    public TFileCreator(File file) {
        this.file = file;
        ByteBuffer buffer = ByteBuffer.allocate(PIECE_LEN);
        byte[] pieceData = new byte[PIECE_LEN];
        byte[] piecesHashes = new byte[0];
        int read, idx = 0;
        try (InputStream input = new FileInputStream(file)) {
            while ((read = input.read(pieceData, 0, buffer.remaining())) != -1) {
                buffer.put(pieceData, 0, read);
                if (buffer.remaining() == 0) {
                    byte[] hash = Util.generateHash(buffer.array());
                    if (hash == null) {
                        return;
                    }
                    piecesHashes = Util.concatByteArrays(piecesHashes, hash);
                    buffer.clear();
                    ++idx;
                }
            }

            if (buffer.remaining() != buffer.capacity()) {
                byte[] hash = Util.generateHash(Arrays.copyOfRange(buffer.array(), 0, buffer.capacity() - buffer.remaining()));
                if (hash == null) {
                    return;
                }
                piecesHashes = Util.concatByteArrays(piecesHashes, hash);
            }
        } catch (IOException e) {
            System.err.println("Caught an exception while creating metainfo file");
            e.printStackTrace();
            return;
        }

        piecesBuffer = ByteBuffer.wrap(piecesHashes);
    }

    private Map<String, Object> createInfoMap() {
        SortedMap<String, Object> info = new TreeMap<>();
        info.put("piece length", PIECE_LEN);
        info.put("pieces", piecesBuffer);
        info.put("name", file.getName());
        info.put("length", file.length());
        return info;
    }

    public byte[] createMetaInfoFile(String announceURL) {
        SortedMap<String, Object> metaInfo = new TreeMap<>();
        metaInfo.put("announce", announceURL);
        metaInfo.put("info", createInfoMap());
        Bencode bencode = new Bencode(true);
        return bencode.encode(metaInfo);
    }

    private ByteBuffer piecesBuffer;
    private final File file;

    public final int PIECE_LEN = 256 * 1024;

}
