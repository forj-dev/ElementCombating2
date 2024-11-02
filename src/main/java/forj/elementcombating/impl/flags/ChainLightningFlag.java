package forj.elementcombating.impl.flags;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.Flag;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Comparator;
import java.util.List;

public class ChainLightningFlag implements Flag {
    protected int spreadCountDown, maxSpreadEntities, spreadTimes;
    protected float range;
    protected ElementDamageInstance damageInstance;
    protected ChainLightningAttackerFlag attackerFlag;
    protected ChainLightningFlag parent;
    protected LivingEntity owner;

    ChainLightningFlag(int spreadCountDown, float range, ElementDamageInstance damageInstance, LivingEntity owner, int maxSpreadEntities, int spreadTimes) {
        this.spreadCountDown = spreadCountDown;
        this.range = range;
        this.damageInstance = damageInstance;
        this.attackerFlag = null;
        this.parent = null;
        this.owner = owner;
        this.maxSpreadEntities = maxSpreadEntities;
        this.spreadTimes = spreadTimes;
    }

    public ChainLightningFlag(int spreadCountDown, float range, ElementDamageInstance damageInstance, ChainLightningFlag parent, LivingEntity owner, int maxSpreadEntities, int spreadTimes) {
        this.spreadCountDown = spreadCountDown;
        this.range = range;
        this.damageInstance = damageInstance;
        this.parent = parent;
        this.attackerFlag = parent.attackerFlag;
        this.owner = owner;
        this.maxSpreadEntities = maxSpreadEntities;
        this.spreadTimes = spreadTimes;
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return true;
        spawnParticles();
        if (spreadCountDown > -1) spreadCountDown--;
        if (spreadCountDown == 0) {
            attackerFlag.activeLightningCount--;
            spread(entity);
        }
        if (attackerFlag.activeLightningCount <= 0) {
            damageInstance.damage(livingEntity, attackerFlag.owner);
            return true;
        }
        return false;
    }

    @Override
    public void onAdd(Entity entity) {
        attackerFlag.activeLightningCount++;
    }

    protected void spread(Entity entity) {
        if (spreadTimes <= 0) return;
        List<LivingEntity> targets = entity.getWorld().getEntitiesByClass(LivingEntity.class,
                Box.of(entity.getPos(), range * 2, range * 2, range * 2),
                e -> e.squaredDistanceTo(entity) <= range * range
                        && ((StatAccessor) e).getFlagManager()
                        .getFlagsByClass(ChainLightningFlag.class,
                                f -> f.attackerFlag == attackerFlag).isEmpty());
        targets.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(entity)));
        for (int i = 0; i < Math.min(maxSpreadEntities, targets.size()); i++) {
            LivingEntity target = targets.get(i);
            ChainLightningFlag flag = new ChainLightningFlag(attackerFlag.spreadCountDown, range, damageInstance, this, target, maxSpreadEntities, spreadTimes - 1);
            ((StatAccessor) target).getFlagManager().add(flag);
            damageInstance.damage(target, attackerFlag.owner);
        }
    }

    private void spawnParticles() {
        if (owner == null || parent == null) return;
        Vec3d pos1 = owner.getPos();
        Vec3d pos2 = parent.owner.getPos();
        final int count = 7;
        for (int i = 0; i < count; i++) {
            double percent = MathHelper.clamp((double) i / count + ElementCombating.RANDOM.nextDouble() - 0.5, 0.0, 1.0);
            double x = MathHelper.lerp(percent, pos1.x, pos2.x) + ElementCombating.RANDOM.nextDouble() - 0.5;
            double y = MathHelper.lerp(percent, pos1.y, pos2.y) + ElementCombating.RANDOM.nextDouble() - 0.5;
            double z = MathHelper.lerp(percent, pos1.z, pos2.z) + ElementCombating.RANDOM.nextDouble() - 0.5;
            Utils.spawnParticle((ServerWorld) owner.getWorld(), new DustParticleEffect(
                    new Vec3f(Vec3d.unpackRgb(damageInstance.effect.getColor())),
                    ElementCombating.RANDOM.nextFloat(1f, 2f)
            ), x, y, z);
        }
    }
}
