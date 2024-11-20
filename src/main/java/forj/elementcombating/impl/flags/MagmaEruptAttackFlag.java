package forj.elementcombating.impl.flags;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class MagmaEruptAttackFlag extends AttackFlag {
    private int lastTicks;
    private final Vec3d motion;
    private Vec3d position;
    private final ElementDamageInstance damageInstance;
    private final float knockback, width, height;
    private final int maxTransferBlocks;

    public MagmaEruptAttackFlag(int lastTicks, Vec3d motion, Vec3d position, ElementDamageInstance damageInstance, float knockback, float width, float height, int maxTransferBlocks) {
        this.lastTicks = lastTicks;
        this.motion = motion;
        this.position = position;
        this.damageInstance = damageInstance;
        this.knockback = knockback;
        this.width = width;
        this.height = height;
        this.maxTransferBlocks = maxTransferBlocks;
    }

    @Override
    public boolean shouldRemove(Entity entity) {
        if (--lastTicks < 0 || !(entity instanceof LivingEntity attacker)) return true;
        if (!move(entity.getWorld())) return true;
        Vec3d checkPos = position.add(ElementCombating.RANDOM.nextDouble() - 0.5, ElementCombating.RANDOM.nextDouble() - 0.5, ElementCombating.RANDOM.nextDouble() - 0.5);
        spawnParticle((ServerWorld) entity.getWorld(), checkPos.x, checkPos.y, checkPos.z);
        Box box = Box.of(checkPos, width, width, width).expand(0, height, 0);
        for (LivingEntity target : entity.getWorld().getEntitiesByClass(LivingEntity.class, box, super::attackPredicate)) {
            if (target.equals(entity)) continue;
            damageInstance.damage(target, attacker);
            target.velocityModified = true;
            target.velocityDirty = true;
            target.addVelocity(0, knockback, 0);
            if (!(damageInstance.effect instanceof ElementType type)) continue;
            ((StatAccessor) attacker).getChargeManager().charge(type, 0.1f);
        }
        return false;
    }

    private void updateY(VoxelShape shape, BlockPos pos) {
        double topY = shape.getMax(Direction.Axis.Y);
        if (topY == Double.NEGATIVE_INFINITY) topY = 0;
        if (topY > 1) topY = 1;
        position = new Vec3d(position.x, pos.getY() + topY, position.z);
    }

    private boolean move(World world) {
        position = position.add(motion);
        BlockPos.Mutable pos = new BlockPos.Mutable(position.x, position.y, position.z);
        VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
        if (VoxelShapes.fullCube().equals(shape)) {
            for (int i = 0; i < maxTransferBlocks && VoxelShapes.fullCube().equals(shape); i++) {
                pos.move(Direction.UP);
                shape = world.getBlockState(pos).getCollisionShape(world, pos);
            }
            if (VoxelShapes.fullCube().equals(shape)) return false;
            updateY(shape, pos);
            return true;
        }
        if (shape.isEmpty()) {
            for (int i = 0; i < maxTransferBlocks && shape.isEmpty(); i++) {
                pos.move(Direction.DOWN);
                shape = world.getBlockState(pos).getCollisionShape(world, pos);
            }
            if (shape.isEmpty()) return false;
            updateY(shape, pos);
            return true;
        }
        updateY(shape, pos);
        return true;
    }

    private static void spawnParticle(ServerWorld world, double x, double y, double z) {
        ParticleS2CPacket packet = new ParticleS2CPacket(ParticleTypes.FLAME, false, x, y, z,
                0, 1, 0, 0.5f, 0);
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos((int) x, (int) y, (int) z)))
            player.networkHandler.sendPacket(packet);
    }
}
