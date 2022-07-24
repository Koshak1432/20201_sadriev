package kosh.torrent;

import com.dampcake.bencode.Bencode;

import java.io.File;
import java.util.*;

public class TFileCreator {
    //как получать pieces???
    //прочитать файл в байтах
    public TFileCreator(File file) {

    }
    private Map<String, Object> createInfoMap(File file) {
        Map<String, Object> info = new HashMap<>();
        info.put("piece length", Constants.pieceLength);
        //todo info.put("pieces", )
        info.put("name", file.getName());
        info.put("length", file.length());
        return info;
    }

    public void createMetaInfoFile(File file, String announceURL) {
        SortedMap<String, Object> metaInfo = new TreeMap<>();
        metaInfo.put("announce", announceURL);
        metaInfo.put("info", createInfoMap(file));
        Bencode bencode = new Bencode();
        byte[] encoded = bencode.encode(metaInfo);

    }



    private String pieces; //it's the concat of all 20-byte hashes
}
