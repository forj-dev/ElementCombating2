package forj.elementcombating.mixin;

import forj.elementcombating.element.ArrowElementAccessor;
import forj.elementcombating.element.StatAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(IllusionerEntity.class)
public class IllusionerEntityMixin {
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void attack(LivingEntity target, float pullProgress, CallbackInfo ci, ItemStack itemStack, PersistentProjectileEntity persistentProjectileEntity, double d, double e, double f, double g) {
        if (!(persistentProjectileEntity instanceof ArrowEntity arrowEntity)) return;
        ((ArrowElementAccessor) arrowEntity).setElementDamageInstance(
                ((StatAccessor) this).getProjectileElement()
        );
    }
}
