import java.security.InvalidKeyException;
import java.util.HashMap;

public class Cache<K, V> implements ICache<K, V> {
    @Override
    public void put(K key, V value) throws InvalidKeyException {
        if (null != map_.putIfAbsent(key, value)) {
            throw new InvalidKeyException();
        }
    }

    @Override
    public V get(K key) throws InvalidKeyException {
        return map_.get(key);
    }

    private final HashMap<K, V> map_ = new HashMap<K, V>();
}
