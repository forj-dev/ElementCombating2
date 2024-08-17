package forj.elementcombating.mixin;

import forj.elementcombating.element.ElementActions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
    public void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        MobEntity _this = (MobEntity) (Object) this;
        if (ElementActions.entityBurst(_this)) {
            cir.setReturnValue(true);
            return;
        }
        if (ElementActions.entitySkill(_this)) {
            cir.setReturnValue(true);
        }
    }
}
