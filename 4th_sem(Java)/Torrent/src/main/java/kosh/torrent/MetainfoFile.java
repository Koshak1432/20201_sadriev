package kosh.torrent;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MetainfoFile {
    @SuppressWarnings("unchecked")
    public MetainfoFile(String metaInfoFileName) {
        try (InputStream in = new FileInputStream(metaInfoFileName)){
            Bencode bencode = new Bencode(StandardCharsets.UTF_8, true);
            Map<String, Object> decoded = bencode.decode(in.readAllBytes(), Type.DICTIONARY);
            info = (HashMap<String, Object>) decoded.get("info");
            infoHash = bencode.encode(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  byte[] getPieces() {
        ByteBuffer buffer = (ByteBuffer) info.get("pieces");
        return buffer.array();
    }
    public long getPieceLen() {
        return (long) info.get("piece length");
    }

    public long getFileLen() {
        return (long) info.get("length");
    }

    public String getName() {
        ByteBuffer buffer = (ByteBuffer) info.get("name");
        return new String(buffer.array());
    }

    public byte[] getInfoHash() {
        return Util.generateHash(infoHash);
    }

    byte[] infoHash;
    private HashMap<String, Object> info = null;
}
