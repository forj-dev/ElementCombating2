package forj.elementcombating.element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CoolDownManager {

    private final Map<ElementType, Entry> skill = new HashMap<>();
    private final Map<ElementType, Entry> burst = new HashMap<>();

    /**
     * Update cool down for 1 tick.
     */
    public void update() {
        if (!this.skill.isEmpty()) {
            Iterator<Map.Entry<ElementType, Entry>> iterator = this.skill.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ElementType, Entry> entry = iterator.next();
                if (entry.getValue().update()) continue;
                this.onUpdate(AttributeType.ENTITY_SKILL, entry.getKey(), 0);
                iterator.remove();
            }
        }
        if (!this.burst.isEmpty()) {
            Iterator<Map.Entry<ElementType, Entry>> iterator = this.burst.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ElementType, Entry> entry = iterator.next();
                if (entry.getValue().update()) continue;
                this.onUpdate(AttributeType.ENTITY_BURST, entry.getKey(), 0);
                iterator.remove();
            }
        }
    }

    public void set(AttributeType attributeType, ElementType elementType, int duration) {
        if (attributeType == AttributeType.ENTITY_BURST) {
            this.burst.put(elementType, new Entry(duration));
        } else {
            this.skill.put(elementType, new Entry(duration));
        }
        this.onUpdate(attributeType, elementType, duration);
    }

    /**
     * Get the progress of cool down.
     * Range: [1.0, 0.0) , 0f means not cooling down
     */
    public float getProgress(AttributeType attributeType, ElementType elementType) {
        Entry entry;
        if (attributeType == AttributeType.ENTITY_BURST) {
            entry = this.burst.get(elementType);
        } else {
            entry = this.skill.get(elementType);
        }
        if (entry == null) return 0f;
        return ((float) entry.tick) / entry.total;
    }

    public void remove(AttributeType attributeType, ElementType elementType) {
        if (attributeType == AttributeType.ENTITY_BURST) {
            this.burst.remove(elementType);
        } else {
            this.skill.remove(elementType);
        }
        this.onUpdate(attributeType, elementType, 0);
    }

    protected void onUpdate(AttributeType attributeType, ElementType elementType, int duration) {
    }

    public static class Entry {
        int tick;
        final int total;

        boolean update() {
            tick--;
            return tick > 0;
        }

        Entry(int tick) {
            this.tick = tick;
            this.total = tick;
        }
    }
}
