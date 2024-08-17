package forj.elementcombating.impl.flags;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.Flag;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class PulseAttackFlag implements Flag {
    private int times, intervalTicks;
    private final float range;
    private final int interval;
    private final ElementDamageInstance damageInstance;

    public PulseAttackFlag(int times, int interval, float range, ElementDamageInstance damageInstance) {
        this.times = times;
        this.interval = interval;
        this.range = range;
        this.damageInstance = damageInstance;
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        if (--intervalTicks >= 0) return false;
        intervalTicks = interval;
        if (--times < 0) return true;
        Box box = Box.of(entity.getPos(), range * 2, range * 2, range * 2);
        for (LivingEntity target : entity.getWorld().getEntitiesByClass(LivingEntity.class, box, e -> true)) {
            if (target.squaredDistanceTo(entity) > range * range) continue;
            damageInstance.damage(target, (LivingEntity) entity);
            ((StatAccessor)entity).getChargeManager().charge((ElementType) damageInstance.effect,0.005f);
        }
        //spawn a particle sphere
        final int particleCount = 25;
        final double angleMultiplier = 2 * Math.PI / particleCount;
        for (int i = 0; i < particleCount; i++) {
            double y = entity.getY() + entity.getHeight() / 2 + Math.sin(i * angleMultiplier) * range;
            for (int j = 0; j < particleCount; j++) {
                double x = entity.getX() + Math.cos(j * angleMultiplier) * Math.cos(i * angleMultiplier) * range;
                double z = entity.getZ() + Math.sin(j * angleMultiplier) * Math.cos(i * angleMultiplier) * range;
                spawnParticle((ServerWorld) entity.getWorld(), new DustParticleEffect(
                        new Vec3f(Vec3d.unpackRgb(damageInstance.effect.getColor())),
                        ElementCombating.RANDOM.nextFloat(1f, 2f)
                ), x, y, z);
            }
        }
        return false;
    }

    private static void spawnParticle(ServerWorld world, ParticleEffect particle, double x, double y, double z) {
        ParticleS2CPacket packet = new ParticleS2CPacket(particle, false, x, y, z,
                0, 0, 0, 1f, 0);
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos((int) x, (int) y, (int) z)))
            player.networkHandler.sendPacket(packet);
    }
}
