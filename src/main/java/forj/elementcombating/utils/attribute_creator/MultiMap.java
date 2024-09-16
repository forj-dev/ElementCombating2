package forj.elementcombating.utils.attribute_creator;

import java.util.HashMap;
import java.util.Map;

public class MultiMap extends HashMap<String, AttributeCreator.Num> {
    private final Map<String, AttributeCreator.Num>[] maps;

    @SafeVarargs
    public MultiMap(Map<String, AttributeCreator.Num>... maps) {
        this.maps = maps;
    }

    @Override
    public AttributeCreator.Num get(Object key) {
        for (Map<String, AttributeCreator.Num> map : maps) {
            AttributeCreator.Num value = map.get(key);
            if (value != null) return value;
        }
        return new AttributeCreator.Num(0);
    }
}
