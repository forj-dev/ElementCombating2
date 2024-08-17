package forj.elementcombating.impl.flags;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
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
import org.jetbrains.annotations.NotNull;

import static net.minecraft.util.math.MathHelper.lerp;

public class SweepAttackFlag extends AttackFlag {
    private float pitch, yaw;
    private double prevX, prevY, prevZ;
    private int lastTicks;
    private final float hitBoxSize, hitBoxDistance, pitchSpeed, yawSpeed;
    private final ElementDamageInstance damageInstance;

    public SweepAttackFlag(int lastTicks, float pitch, float yaw, float pitchSpeed, float yawSpeed, float hitBoxSize, float hitBoxDistance, ElementDamageInstance damageInstance, LivingEntity user) {
        this.lastTicks = lastTicks;
        this.pitch = pitch;
        this.yaw = yaw;
        this.pitchSpeed = pitchSpeed;
        this.yawSpeed = yawSpeed;
        this.hitBoxSize = hitBoxSize;
        this.hitBoxDistance = hitBoxDistance;
        this.damageInstance = damageInstance;
        Vec3d pos = getLocalPos(user);
        prevX = pos.x;
        prevY = pos.y;
        prevZ = pos.z;
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return true;
        }
        if (--lastTicks < 0) {
            this.isRemoved = true;
            return true;
        }
        pitch += pitchSpeed;
        yaw += yawSpeed;
        Vec3d pos = getLocalPos(entity);
        final int lerpCount = 5;
        for (int i = 0; i < lerpCount; i++) {
            attack(lerp(i / (double) lerpCount, prevX, pos.x),
                    lerp(i / (double) lerpCount, prevY, pos.y),
                    lerp(i / (double) lerpCount, prevZ, pos.z),
                    (LivingEntity) entity);
        }
        prevX = pos.x;
        prevY = pos.y;
        prevZ = pos.z;
        return false;
    }

    @NotNull
    private Vec3d getLocalPos(Entity entity) {
        double realPitch = pitch;
        double realYaw = yaw + entity.getYaw() + 90;
        realPitch = Math.toRadians(realPitch);
        realYaw = Math.toRadians(realYaw);
        double cosPitch = Math.cos(realPitch);
        return new Vec3d(
                hitBoxDistance * cosPitch * Math.cos(realYaw),
                hitBoxDistance * Math.sin(realPitch),
                hitBoxDistance * cosPitch * Math.sin(realYaw)
        );
    }

    private void attack(double x, double y, double z, LivingEntity entity) {
        double rx = x + entity.getX();
        double ry = y + entity.getY() + entity.getEyeHeight(EntityPose.STANDING);
        double rz = z + entity.getZ();
        Box hitBox = Box.of(new Vec3d(rx, ry, rz), hitBoxSize, hitBoxSize, hitBoxSize);
        final double strength = 2.5f;
        Vec3f color = new Vec3f(Vec3d.unpackRgb(this.damageInstance.effect.getColor()));
        spawnParticle((ServerWorld) entity.getWorld(), new DustParticleEffect(color, ElementCombating.RANDOM.nextFloat(1f, 2f)), rx, ry, rz, x * strength, y * strength, z * strength);
        for (LivingEntity target : entity.getWorld().getEntitiesByClass(LivingEntity.class, hitBox, super::attackPredicate)) {
            if (entity.equals(target)) continue;
            if (this.damageInstance.effect instanceof ElementType type) {
                ((StatAccessor) entity).getChargeManager().charge(type, 0.1f);
            }
            this.damageInstance.damage(target, entity);
        }
    }

    private static void spawnParticle(ServerWorld world, ParticleEffect particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ParticleS2CPacket packet = new ParticleS2CPacket(particle, false, x, y, z,
                (float) velocityX, (float) velocityY, (float) velocityZ, 1f, 0);
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos((int) x, (int) y, (int) z)))
            player.networkHandler.sendPacket(packet);
    }
}
