package kosh.torrent;

import com.dampcake.bencode.BencodeInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MetainfoFile {
    public MetainfoFile(String metainfoFileName) {
        try (InputStream in = new FileInputStream(metainfoFileName)){
            BencodeInputStream bencodeInputStream = new BencodeInputStream(in, StandardCharsets.UTF_8, true);
            Map<String, Object> decoded = bencodeInputStream.readDictionary();
            info = (HashMap<String, Object>) decoded.get("info");
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
        byte[] infoBytes = serialize(info);
        return Util.generateHash(infoBytes);
    }

    private byte[] serialize(Object o) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(info);
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> info;
}
