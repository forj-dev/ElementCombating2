package forj.elementcombating.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Array-List; Elements in it can be accessed by index or key.
 *
 * @param <K> Key Type
 * @param <V> Value Type
 */
public class MapList<K, V> extends ArrayList<V> {
    private final Map<K, Integer> map = new HashMap<>();

    public MapList() {
    }

    public void put(K key, V value) {
        this.map.put(key, this.size());
        super.add(value);
    }

    public V get(K key) throws IllegalArgumentException {
        Integer index = this.map.get(key);
        if (index == null) throw new IllegalArgumentException("Map doesn't contain the key.");
        return this.get(index);
    }

    @Override
    public boolean add(V v) {
        throw new RuntimeException();
    }

    @Override
    public void add(int index, V element) {
        throw new RuntimeException();
    }
}
