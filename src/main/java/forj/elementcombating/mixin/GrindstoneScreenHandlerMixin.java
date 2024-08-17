package forj.elementcombating.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin {
    @Shadow
    @Final
    Inventory input;

    @Inject(method = "grind", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;removeSubNbt(Ljava/lang/String;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void grind(ItemStack item, int damage, int amount, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        itemStack.removeSubNbt("element_attribute");
    }

    @ModifyVariable(method = "updateResult", at = @At(value = "STORE"), ordinal = 2)
    public boolean updateResult(boolean bl4) {
        ItemStack itemStack = this.input.getStack(0);
        ItemStack itemStack2 = this.input.getStack(1);
        if (!itemStack.isEmpty() && itemStack.getSubNbt("element_attribute") != null || !itemStack2.isEmpty() && itemStack2.getSubNbt("element_attribute") != null)
            return false;
        return bl4;
    }
}
