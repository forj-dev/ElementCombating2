package forj.elementcombating.mixin;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.DamageInfoAccessor;
import forj.elementcombating.element.ElementEffect;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.entity.entity.ElementDamageCrystalEntity;
import forj.elementcombating.item.Items;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(EntityType<?> entityType, World world, CallbackInfo ci) {
        if (this.getServer() == null) return;
        this.setServerEntity((LivingEntity) (Object) this);
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "dropLoot", at = @At("RETURN"))
    public void dropLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        if (!causedByPlayer) return;
        if (!((LivingEntity) (Object) this instanceof HostileEntity)) return;
        Entity entity = source.getSource();
        if (!(entity instanceof LivingEntity attacker)) return;
        int looting = EnchantmentHelper.getLooting(attacker);
        int count = Math.min(12, ElementCombating.RANDOM.nextInt(looting * 3 + 3));
        this.dropStack(new ItemStack(Items.ELEMENT_CRYSTAL, count));
    }

    @Inject(method = "tickStatusEffects", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/LivingEntity;onStatusEffectRemoved(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void tickStatusEffects(CallbackInfo ci, Iterator<StatusEffect> iterator, StatusEffect statusEffect, StatusEffectInstance statusEffectInstance) {
        if (!(statusEffect instanceof ElementEffect elementEffect)) return;
        ElementEffect.syncEffect((LivingEntity) (Object) this, elementEffect, 0);
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    public float damage(float amount, DamageSource source) {
        if (((DamageInfoAccessor) source).bypassesShield())
            return amount;
        return ((StatAccessor) this).getShieldManager().attack(amount);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (((DamageInfoAccessor) source).bypassesShield()) return;
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity) {
            Set<ElementDamageCrystalEntity.ElementDamageFlag> flags = ((StatAccessor) attacker).getFlagManager().getFlagsByClass(ElementDamageCrystalEntity.ElementDamageFlag.class, t -> true);
            for (ElementDamageCrystalEntity.ElementDamageFlag flag : flags) {
                flag.type.attack((LivingEntity) (Object) this, (LivingEntity) attacker, 1, 60, 2f);
            }
        }
    }
}
