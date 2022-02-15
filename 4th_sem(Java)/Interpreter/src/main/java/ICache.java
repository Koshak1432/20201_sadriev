import java.security.InvalidKeyException;

public interface ICache<K, V> {
    public void put(K key, V value) throws InvalidKeyException;

    public V get(K key) throws InvalidKeyException;
}
