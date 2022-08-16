package kosh.torrent;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Util {
    public static byte[] generateHash(byte[] inputToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(inputToHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Can't generate hash");
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] concatByteArrays(byte[] a1, byte[] a2) {
        byte[] result = new byte[a1.length + a2.length];
        System.arraycopy(a1, 0, result, 0, a1.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }

    public static int convertToInt(byte[] value) {
        assert value.length == Integer.BYTES;
        ByteBuffer bb = ByteBuffer.wrap(value);
        return bb.getInt();

    }

    public static byte[] convertToByteArr(int value) {
        ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES);
        bb.putInt(value);
        return bb.array();
    }
}
