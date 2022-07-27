package kosh.torrent;

import com.dampcake.bencode.Bencode;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class TFileCreator {
    public TFileCreator(File file) {
        this.file = file;
        ByteBuffer buffer = ByteBuffer.allocate(Constants.pieceLength);
        byte[] pieceData = new byte[Constants.pieceLength];
        int read;
        try (InputStream input = new FileInputStream(file)) {
            while ((read = input.read(pieceData, 0, buffer.remaining())) != -1) {
                System.out.println(read);
                buffer.put(pieceData, 0, read);
                if (buffer.remaining() == 0) {
                    pieces.add(new Piece(buffer.array()));
                    buffer.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (buffer.remaining() != buffer.capacity()) {
            pieces.add(new Piece(Util.subArray(buffer.array(), 0, buffer.capacity() - buffer.remaining())));
        }

        piecesHashes = joinHashes();
    }

    private byte[] joinHashes() {
        byte[] hashes = new byte[0];
        for (Piece piece : pieces) {
            hashes = Util.concatByteArrays(hashes, piece.getSHA1hash());
        }
        return hashes;
    }

    private Map<String, Object> createInfoMap() {
        SortedMap<String, Object> info = new TreeMap<>();
        info.put("piece length", Constants.pieceLength);
        info.put("pieces", piecesHashes);
        info.put("name", file.getName());
        info.put("length", file.length());
        System.out.println("DEBUG");
        System.out.println(file.length());
        System.out.println("PiecesHashes len:" + piecesHashes.length);
        System.out.println("number of pieces:" + pieces.size());
        return info;
    }

    public byte[] createMetaInfoFile(String announceURL) {
        SortedMap<String, Object> metaInfo = new TreeMap<>();
        metaInfo.put("announce", announceURL);
        metaInfo.put("info", createInfoMap());
        Bencode bencode = new Bencode();
        return bencode.encode(metaInfo);
    }

    private final byte[] piecesHashes; //it's the concat of all 20-byte hashes
    private final ArrayList<Piece> pieces = new ArrayList<>();
    private final File file;
}
