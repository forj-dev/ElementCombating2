package forj.elementcombating.impl;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementRegistry;
import forj.elementcombating.element.ElementType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class Utils {
    public static final TrackedDataHandler<Optional<ElementType>> OptionalElementTypeTrackedDataHandler = new TrackedDataHandler<>() {
        @Override
        public void write(PacketByteBuf buf, Optional<ElementType> value) {
            buf.writeBoolean(value.isPresent());
            value.ifPresent(type -> buf.writeString(type.getId()));
        }

        @Override
        public Optional<ElementType> read(PacketByteBuf buf) {
            if (!buf.readBoolean()) {
                return Optional.empty();
            }
            String id = buf.readString();
            return Optional.of(ElementRegistry.getElementTypes().get(id));
        }

        @Override
        public Optional<ElementType> copy(Optional<ElementType> value) {
            return value;
        }
    };

    public static void knockback(LivingEntity target, LivingEntity attacker, double strength) {
        target.takeKnockback(strength, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
    }

    public static void spawnParticle(ServerWorld world, ParticleEffect particle, double x, double y, double z) {
        ParticleS2CPacket packet = new ParticleS2CPacket(particle, false, x, y, z,
                0, 0, 0, 1f, 0);
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos((int) x, (int) y, (int) z)))
            player.networkHandler.sendPacket(packet);
    }

    public static Vec3d randomVec3d(double scale) {
        return new Vec3d(ElementCombating.RANDOM.nextDouble() - 0.5, ElementCombating.RANDOM.nextDouble() - 0.5, ElementCombating.RANDOM.nextDouble() - 0.5).normalize().multiply(scale);
    }

    public static Vec3d directionVec3d(double scale, double pitch, double yaw) {
        pitch = Math.toRadians(pitch);
        yaw = Math.toRadians(yaw);
        double x = -Math.cos(pitch) * Math.sin(yaw) * scale;
        double y = -Math.sin(pitch) * scale;
        double z = Math.cos(pitch) * Math.cos(yaw) * scale;
        return new Vec3d(x, y, z);
    }

    public static void init() {
        TrackedDataHandlerRegistry.register(OptionalElementTypeTrackedDataHandler);
    }
}
