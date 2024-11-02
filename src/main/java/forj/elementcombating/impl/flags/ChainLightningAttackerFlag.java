package forj.elementcombating.impl.flags;

import forj.elementcombating.impl.ElementDamageInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ChainLightningAttackerFlag extends ChainLightningFlag {
    protected int activeLightningCount = 0;

    public ChainLightningAttackerFlag(int spreadCountDown, float range, ElementDamageInstance damageInstance, LivingEntity attacker, int maxSpreadEntities, int spreadTimes) {
        super(spreadCountDown, range, damageInstance, attacker, maxSpreadEntities, spreadTimes);
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        return activeLightningCount <= 0;
    }

    @Override
    public void onAdd(Entity entity) {
        this.attackerFlag = this;
        activeLightningCount = 0;
        super.spread(entity);
    }

    @Override
    public void onRemove(Entity entity) {
        activeLightningCount = 0;
    }
}
