package forj.elementcombating.impl.flags;

import forj.elementcombating.element.Flag;
import forj.elementcombating.element.FlagManager;
import forj.elementcombating.element.StatAccessor;
import net.minecraft.entity.Entity;

public abstract class AttackFlag implements Flag {
    public boolean isRemoved = false;

    public boolean isAttacked(Entity target) {
        FlagManager manager = ((StatAccessor) target).getFlagManager();
        //if there is an AttackedFlag of this flag, return false
        //to make sure a flag will only attack an entity once
        return !manager.getFlagsByClass(AttackedFlag.class, flag -> flag.attackFlag == this).isEmpty();
    }

    public void markAttacked(Entity target) {
        FlagManager manager = ((StatAccessor) target).getFlagManager();
        manager.add(new AttackedFlag(this));
    }

    public boolean attackPredicate(Entity target) {
        if (isAttacked(target)) return false;
        markAttacked(target);
        return true;
    }
}
