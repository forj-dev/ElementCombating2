package forj.elementcombating.impl.flags;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.FlagManager;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class SpurtAttackFlag extends AttackFlag {
    private int lastTicks, speedUpTicks;
    private final int totalSpeedUpTicks;
    private final Vec3d motion, direction;
    private final float removeSpeed;
    private final ElementDamageInstance damageInstance;
    private final float knockback;

    public SpurtAttackFlag(int lastTicks, int speedUpTicks, Vec3d motion, float removeSpeed, ElementDamageInstance damageInstance, float knockback) {
        this.lastTicks = lastTicks;
        this.speedUpTicks = speedUpTicks;
        this.totalSpeedUpTicks = speedUpTicks;
        this.motion = motion;
        this.direction = motion.normalize();
        this.removeSpeed = removeSpeed;
        this.damageInstance = damageInstance;
        this.knockback = knockback;
    }

    @Override
    public void markAttacked(Entity target) {
        FlagManager manager = ((StatAccessor) target).getFlagManager();
        manager.add(new TimedAttackedFlag(this, 10));
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        if (--lastTicks <= 0 || !(entity instanceof LivingEntity attacker)) {
            this.isRemoved = true;
            return true;
        }
        if (--speedUpTicks > 0) {
            attacker.setVelocity(entity.getVelocity().add(motion.multiply((double) speedUpTicks / totalSpeedUpTicks)));
            attacker.velocityModified = true;
            attacker.velocityDirty = true;
        }
        for (LivingEntity target : attacker.world.getEntitiesByClass(LivingEntity.class, entity.getBoundingBox().expand(0.8), super::attackPredicate)) {
            if (target == attacker) continue;
            knockback(target, attacker, knockback);
            this.damageInstance.damage(target, attacker);
            if (damageInstance.effect instanceof ElementType elementType) {
                ((StatAccessor) attacker).getChargeManager().charge(elementType, 0.1f);
            }
        }
        Vec3f color = new Vec3f(Vec3d.unpackRgb(this.damageInstance.effect.getColor()));
        spawnParticle((ServerWorld) attacker.world, new DustParticleEffect(color, ElementCombating.RANDOM.nextFloat(1f, 2f)), entity.getX(), entity.getY() + 0.5 * entity.getHeight(), entity.getZ(), Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
        double speed = entity.getVelocity().dotProduct(direction);
        if (speed < removeSpeed && speedUpTicks <= 0) {
            this.isRemoved = true;
            return true;
        }
        return false;
    }

    private static void knockback(LivingEntity entity, LivingEntity attacker, float knockback) {
        Vec3d motion = attacker.getVelocity();
        double x = entity.getX() - attacker.getX() + 0.7 * motion.x;
        double z = entity.getZ() - attacker.getZ() + 0.7 * motion.z;
        entity.takeKnockback(knockback, x, z);
    }

    private static void spawnParticle(ServerWorld world, ParticleEffect particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ParticleS2CPacket packet = new ParticleS2CPacket(particle, false, x, y, z,
                (float) velocityX, (float) velocityY, (float) velocityZ, 1f, 0);
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos((int) x, (int) y, (int) z)))
            player.networkHandler.sendPacket(packet);
    }
}
