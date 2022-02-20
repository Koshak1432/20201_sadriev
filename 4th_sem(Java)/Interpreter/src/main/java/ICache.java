import java.security.InvalidKeyException;

public interface ICache<K, V> {
    void put(K key, V value) throws InvalidKeyException;
    V get(K key) throws InvalidKeyException;
}
