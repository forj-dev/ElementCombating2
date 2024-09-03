package forj.elementcombating.mixin;

import forj.elementcombating.element.ElementRegistry;
import forj.elementcombating.impl.ElementDamageInstance;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin {
    @Inject(method = "makeInitialWeapon", at = @At("RETURN"), cancellable = true)
    public void makeInitialWeapon(CallbackInfoReturnable<ItemStack> cir){
        ItemStack stack = cir.getReturnValue();
        if (stack.getItem() != Items.CROSSBOW) {
            cir.setReturnValue(stack);
            return;
        }
        if (Math.random() <= 0.8){
            ElementDamageInstance instance = new ElementDamageInstance(
                    ElementRegistry.randomElement(),
                    1, 100, 1f
            );
            stack.setSubNbt("projectile_element",instance.save());
            cir.setReturnValue(stack);
        }
    }
}
