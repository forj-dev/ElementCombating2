package forj.elementcombating.mixin;

import forj.elementcombating.element.ElementAttribute;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    @Nullable
    public abstract NbtCompound getNbt();

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void getTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ElementAttribute attribute;
        try {
            attribute = new ElementAttribute(Objects.requireNonNull(this.getNbt()).getCompound("element_attribute"));
        } catch (RuntimeException e) {
            return;
        }
        List<Text> list = cir.getReturnValue();
        list.add(new TranslatableText("tooltip.element_combating.attack_mode")
                .append(new TranslatableText("mode.element_combating." + attribute.getAttackMode().getAttackMode().getId())));
        list.add(new TranslatableText("tooltip.element_combating.element_type")
                .append(new TranslatableText("effect.element_combating." + attribute.getElementType().getId())));
        list.add(new TranslatableText("tooltip.element_combating.level")
                .append(String.valueOf(attribute.getLevel())));
        cir.setReturnValue(list);
    }
}
