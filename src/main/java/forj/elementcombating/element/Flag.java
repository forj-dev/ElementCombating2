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

    /**
     * Called when this flag is removed from FlagManager
     *
     * @param entity owner of this flag
     */
    default void onRemove(Entity entity) {
    }

    /**
     * Called when this flag is added to FlagManager
     *
     * @param entity owner of this flag
     */
    default void onAdd(Entity entity) {
    }
}
