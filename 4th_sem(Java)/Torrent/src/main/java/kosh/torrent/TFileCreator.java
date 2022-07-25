package kosh.torrent;

import com.dampcake.bencode.Bencode;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class TFileCreator {
    public TFileCreator(File file) {
        ByteBuffer buffer = ByteBuffer.allocate(Constants.pieceLength);
        byte[] pieceData = new byte[Constants.pieceLength];
        int read;
        try (InputStream input = new FileInputStream(file)) {
            while ((read = input.read(pieceData, 0, buffer.remaining())) != -1) {
                buffer.put(pieceData, 0, read);
                if (buffer.remaining() == 0) {
                    pieces.add(new Piece(buffer.array()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (buffer.remaining() != buffer.capacity()) {
            pieces.add(new Piece(Util.subArray(buffer.array(), 0, buffer.capacity() - buffer.remaining())));
        }

        joinHashes();
    }

    private void joinHashes() {
        for (Piece piece : pieces) {
            piecesHashes.add(piece.getSHA1hash());
        }
    }

    private Map<String, Object> createInfoMap(File file) {
        SortedMap<String, Object> info = new TreeMap<>();
        info.put("piece length", Constants.pieceLength);
        info.put("pieces", piecesHashes);
        info.put("name", file.getName());
        info.put("length", file.length());
        return info;
    }

    //to create torrent pass this method to fos.write()
    public byte[] createMetaInfoFile(File file, String announceURL) {
        SortedMap<String, Object> metaInfo = new TreeMap<>();
        metaInfo.put("announce", announceURL);
        metaInfo.put("info", createInfoMap(file));
        Bencode bencode = new Bencode();
        return bencode.encode(metaInfo);
    }



//    private String pieces; //it's the concat of all 20-byte hashes
    private final ArrayList<byte[]> piecesHashes = new ArrayList<>();
    private final ArrayList<Piece> pieces = new ArrayList<>();
}
