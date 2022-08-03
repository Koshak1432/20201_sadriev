package kosh.torrent;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.BencodeInputStream;
import com.dampcake.bencode.Type;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    public byte[] getPieces() {
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

    private HashMap<String, Object> info = new HashMap<>();
}
