package forj.elementcombating.element;

import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class FlagManager {
    private final Entity entity;
    private List<Flag> flags = new ArrayList<>();

    public FlagManager(Entity entity) {
        this.entity = entity;
    }

    public void add(Flag flag) {
        this.flags.add(flag);
        flag.onAdd(this.entity);
    }

    @SuppressWarnings({"unchecked", "ForLoopReplaceableByForEach"})
    public <F extends Flag> void removeByClass(Class<F> clazz, Predicate<F> predicate) {
        List<Flag> new_flags = new ArrayList<>();
        for (int i = 0; i < this.flags.size(); i++) {
            Flag flag = this.flags.get(i);
            if (!clazz.isInstance(flag) || !predicate.test((F) flag)) {
                new_flags.add(flag);
            }
            else {
                flag.onRemove(this.entity);
            }
        }
        this.flags = new_flags;
    }

    @SuppressWarnings("unchecked")
    public <F extends Flag> Set<F> getFlagsByClass(Class<F> clazz, Predicate<F> predicate) {
        Set<F> result = new HashSet<>();
        for (Flag flag : this.flags) {
            if (!clazz.isInstance(flag)) continue;
            if (!predicate.test((F) flag)) continue;
            result.add((F) flag);
        }
        return result;
    }

    /**
     * This method should be called every tick.
     */
    public void update() {
        this.removeByClass(Flag.class, flag -> flag.shouldRemove(this.entity));
    }

    public void discard() {
        this.removeByClass(Flag.class, flag -> true);
    }
}
