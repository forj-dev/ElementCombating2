package forj.elementcombating.impl.flags;

import forj.elementcombating.element.Flag;
import net.minecraft.entity.Entity;

public class AttackedFlag implements Flag {
    public final AttackFlag attackFlag;

    public AttackedFlag(AttackFlag source) {
        this.attackFlag = source;
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        return this.attackFlag.isRemoved;
    }
}
