import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> implements ICache<K, V> {

    @Override
    public void put(K key, V value) throws InvalidKeyException {
        if (null != map_.putIfAbsent(key, value)) {
            throw new InvalidKeyException();
        }
    }

    @Override
    public V get(K key) throws InvalidKeyException {
        if (map_.containsKey(key)) {
            return map_.get(key);
        }
        throw new InvalidKeyException();
    }

    private final Map<K, V> map_ = new HashMap<K, V>();
}
