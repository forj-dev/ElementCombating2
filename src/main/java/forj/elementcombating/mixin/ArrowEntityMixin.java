package forj.elementcombating.mixin;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ArrowElementAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends Entity implements ArrowElementAccessor {
    @Unique
    private ElementDamageInstance elementDamageInstance = null;

    public ArrowEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound elementNbt = nbt.getCompound("projectile_element");
        if (elementNbt != null) {
            try {
                setElementDamageInstance(new ElementDamageInstance(elementNbt));
            } catch (IllegalArgumentException e) {
                setElementDamageInstance(null);
            }
        } else {
            setElementDamageInstance(null);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (elementDamageInstance != null) {
            nbt.put("projectile_element", elementDamageInstance.save());
        }
    }

    @Unique
    @Override
    public void setElementDamageInstance(ElementDamageInstance damageInstance) {
        elementDamageInstance = damageInstance;
        if (world.isClient) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(getId());
        if (damageInstance != null) {
            buf.writeBoolean(true);
            buf.writeNbt(damageInstance.save());
        } else {
            buf.writeBoolean(false);
        }
        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) getWorld(), getBlockPos())) {
            ServerPlayNetworking.send(player, ElementCombating.ArrowElementSync, buf);
        }
    }

    @Inject(method = "onHit", at = @At("RETURN"))
    public void onHit(LivingEntity target, CallbackInfo ci) {
        if (elementDamageInstance != null) {
            if (((ArrowEntity) (Object) this).getOwner() instanceof LivingEntity attacker) {
                elementDamageInstance.damage(target, attacker);
            } else {
                elementDamageInstance.damage(target);
            }
        }
    }

    @Inject(method = "spawnParticles", at = @At("HEAD"))
    public void spawnParticles(int amount, CallbackInfo ci) {
        if (amount <= 0) return;
        if (elementDamageInstance == null) return;
        int color = elementDamageInstance.effect.getColor();
        double r = (color >> 16 & 255) / 255.0;
        double g = (color >> 8 & 255) / 255.0;
        double b = (color & 255) / 255.0;
        for (int i = 0; i < amount; i++) {
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), r, g, b);
        }
    }
}
