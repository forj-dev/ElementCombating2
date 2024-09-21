package forj.elementcombating.impl.flags;

import net.minecraft.entity.Entity;

public class TimedAttackedFlag extends AttackedFlag {
    private int lastTicks;

    public TimedAttackedFlag(AttackFlag source, int ticks) {
        super(source);
        lastTicks = ticks;
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        lastTicks--;
        return super.shouldRemove(entity) || lastTicks <= 0;
    }
}
