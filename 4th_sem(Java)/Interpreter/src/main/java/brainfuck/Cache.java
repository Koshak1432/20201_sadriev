package brainfuck;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> implements ICache<K, V> {

    @Override
    public void put(K key, V value) {
        if (!map_.containsKey(key)) {
            map_.put(key, value);
        }
    }

    @Override
    public V get(K key) {
        if (map_.containsKey(key)) {
            return map_.get(key);
        }
        return null;
    }

    private final Map<K, V> map_ = new HashMap<>();
}
