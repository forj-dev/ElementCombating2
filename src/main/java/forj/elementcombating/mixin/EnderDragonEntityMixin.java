package forj.elementcombating.mixin;

import forj.elementcombating.element.ElementEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends LivingEntity {
    protected EnderDragonEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "addStatusEffect", at = @At("HEAD"), cancellable = true)
    public void addStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (!(effect.getEffectType() instanceof ElementEffect)) return;
        cir.setReturnValue(super.addStatusEffect(effect, source));
    }
}
