package forj.elementcombating.mixin;

import forj.elementcombating.element.*;
import forj.elementcombating.element.network.ServerEntityShieldManager;
import forj.elementcombating.element.network.ServerPlayerChargeManager;
import forj.elementcombating.element.network.ServerPlayerCoolDownManager;
import forj.elementcombating.impl.ElementDamageInstance;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public abstract class EntityMixin implements StatAccessor {

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @SuppressWarnings("UnusedReturnValue")
    @Shadow
    @Nullable
    public abstract ItemEntity dropStack(ItemStack stack);

    @Unique
    private ChargeManager chargeManager;
    @Unique
    private CoolDownManager coolDownManager;
    @Unique
    private ElementAttribute skillAttribute;
    @Unique
    private ElementAttribute burstAttribute;
    @Unique
    private ElementDamageInstance projectileElement;
    @Unique
    private ShieldManager shieldManager;
    @Unique
    private FlagManager flagManager;
    @Unique
    private ReactionManager reactionManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(EntityType<?> type, World world, CallbackInfo ci) {
        this.chargeManager = new ChargeManager();
        this.coolDownManager = new CoolDownManager();
        this.shieldManager = new ShieldManager();
        this.flagManager = new FlagManager((Entity) (Object) this);
        this.reactionManager = new ReactionManager();
        this.skillAttribute = new ElementAttribute(AttributeType.ENTITY_SKILL, 1, ElementProviders.fromEntity(type));
        this.burstAttribute = new ElementAttribute(AttributeType.ENTITY_BURST, 1, ElementProviders.fromEntity(type));
        this.projectileElement = new ElementDamageInstance(
                ElementRegistry.randomElement(),
                1, 100, 1f
        );
    }

    @Unique
    public void setServerEntity(LivingEntity entity) {
        this.shieldManager = new ServerEntityShieldManager(entity);
    }

    /**
     * Only for {@link ServerPlayerEntityMixin}.
     */
    @Unique
    public void setServerPlayer(ServerPlayerEntity player) {
        this.chargeManager = new ServerPlayerChargeManager(player);
        this.coolDownManager = new ServerPlayerCoolDownManager(player);
    }

    @Unique
    public void copyCustomManagerFrom(@NotNull Entity _oldEntity) {
        EntityMixin oldEntity = (EntityMixin) (Object) _oldEntity;
        this.chargeManager = oldEntity.chargeManager;
        this.coolDownManager = oldEntity.coolDownManager;
        this.shieldManager = oldEntity.shieldManager;
        this.burstAttribute = oldEntity.burstAttribute;
        this.skillAttribute = oldEntity.skillAttribute;
        this.projectileElement = oldEntity.projectileElement;
    }

    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(Entity.RemovalReason reason, CallbackInfo ci) {
        this.flagManager.discard();
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.chargeManager.load(nbt.getList("element_charge", NbtType.COMPOUND));
        try {
            this.skillAttribute = new ElementAttribute(nbt.getCompound("element_skill"));
        } catch (RuntimeException ignored) {
        }
        try {
            this.burstAttribute = new ElementAttribute(nbt.getCompound("element_burst"));
        } catch (RuntimeException ignored) {
        }
        try {
            this.projectileElement = new ElementDamageInstance(nbt.getCompound("element_projectile"));
        } catch (RuntimeException ignored) {
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.put("element_charge", this.chargeManager.save());
        nbt.put("element_skill", this.skillAttribute.save());
        nbt.put("element_burst", this.burstAttribute.save());
        nbt.put("element_projectile", this.projectileElement.save());
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void baseTick(CallbackInfo ci) {
        this.coolDownManager.update();
        this.shieldManager.update();

        if (this.getServer() == null) return;

        this.flagManager.update();
        this.reactionManager.update();
    }

    @Unique
    @Override
    public ChargeManager getChargeManager() {
        return chargeManager;
    }

    @Unique
    @Override
    public CoolDownManager getCoolDownManager() {
        return coolDownManager;
    }

    @Unique
    @Override
    public ShieldManager getShieldManager() {
        return shieldManager;
    }

    @Unique
    @Override
    public FlagManager getFlagManager() {
        return flagManager;
    }

    @Unique
    @Override
    public void scheduleReaction(ElementReactionInstance reaction) {
        if (this.getServer() == null) return;
        this.reactionManager.scheduleReaction(reaction);
    }

    @Unique
    @Override
    public ElementAttribute getSkillAttribute() {
        return skillAttribute;
    }

    @Unique
    @Override
    public ElementAttribute getBurstAttribute() {
        return burstAttribute;
    }

    /**
     * Only for {@link forj.elementcombating.element.network.S2CPacketReceiver}
     */
    @Unique
    @Override
    public void setSkillAttribute(ElementAttribute attribute) {
        this.skillAttribute = attribute;
    }

    /**
     * Only for {@link forj.elementcombating.element.network.S2CPacketReceiver}
     */
    @Unique
    @Override
    public void setBurstAttribute(ElementAttribute attribute) {
        this.burstAttribute = attribute;
    }

    @Unique
    @Override
    public ElementDamageInstance getProjectileElement() {
        return projectileElement;
    }

    @Unique
    @Override
    public void setProjectileElement(ElementDamageInstance element) {
        this.projectileElement = element;
    }

}
