package forj.elementcombating.mixin;

import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.item.Items;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    @Shadow
    private int repairItemUsage;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void updateResult(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3) {
        if (itemStack.getItem() instanceof SwordItem && itemStack3.isOf(Items.ELEMENT_GEM)) {
            int gem_lv, sword_lv;
            if (itemStack.getNbt() != null && itemStack3.getNbt() != null) {
                gem_lv = itemStack3.getNbt().getInt("level");
                sword_lv = itemStack.getNbt().getCompound("element_attribute").getInt("level");
            } else {
                this.levelCost.set(0);
                this.output.setStack(0, ItemStack.EMPTY);
                ci.cancel();
                return;
            }
            if (gem_lv != sword_lv + 1) {
                this.levelCost.set(0);
                this.output.setStack(0, ItemStack.EMPTY);
                ci.cancel();
                return;
            }
            if (sword_lv == 0) {
                itemStack2.setSubNbt("element_attribute", new ElementAttribute(AttributeType.ITEM_SKILL, 1).save());
                this.levelCost.set(3);
            } else {
                //noinspection DataFlowIssue
                NbtCompound sword = itemStack.getSubNbt("element_attribute").copy();
                sword.putInt("level", gem_lv);
                itemStack2.setSubNbt("element_attribute", sword);
                this.levelCost.set(2 * gem_lv);
            }
            this.repairItemUsage = 1;
            this.output.setStack(0, itemStack2);
            this.sendContentUpdates();
            ci.cancel();
        }
    }
}
