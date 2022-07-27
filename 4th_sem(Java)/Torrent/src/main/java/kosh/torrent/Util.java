package kosh.torrent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Util {
    /*
    * if there is only one block of data, use digest without update
    * if there are several blocks of data, use update for all blocks and then digest
    * (habr)
     */
    public static byte[] generateHash(byte[] inputToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(inputToHash);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Can't generate hash");
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] subArray(byte[] src, int offset, int length){
        byte[] sub = new byte[length];
        System.arraycopy(src, offset, sub, 0, length);
        return sub;
    }

    public static byte[] generateId() {
        byte[] id = new byte[20];
        Random random = new Random(System.currentTimeMillis());
        random.nextBytes(id);
        return id;
    }

    public static byte[] concatByteArrays(byte[] a1, byte[] a2) {
        byte[] result = new byte[a1.length + a2.length];
        System.arraycopy(a1, 0, result, 0, a1.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }
}
