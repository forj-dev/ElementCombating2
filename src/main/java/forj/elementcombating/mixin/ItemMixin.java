package forj.elementcombating.mixin;

import forj.elementcombating.element.ElementActions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (hand != Hand.MAIN_HAND) return;
        ItemStack item = user.getStackInHand(Hand.MAIN_HAND);
        if (!(item.getItem() instanceof SwordItem)) return;
        cir.setReturnValue(new TypedActionResult<>(ElementActions.itemSkill(user, item), item));
    }
}
