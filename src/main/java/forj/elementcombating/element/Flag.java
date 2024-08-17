package forj.elementcombating.element;

import net.minecraft.entity.Entity;

public interface Flag {
    /**
     * Called every FlagManager update(every tick)
     *
     * @param entity owner of this flag
     * @return if true, FlagManager will remove this flag
     */
    boolean shouldRemove(Entity entity);
}
