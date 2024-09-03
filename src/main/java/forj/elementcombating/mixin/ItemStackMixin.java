package forj.elementcombating.mixin;

import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.impl.ElementDamageInstance;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Shadow
    public abstract Item getItem();

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void getTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if (this.getItem() instanceof SwordItem) {
            cir.setReturnValue(appendSwordTooltip(cir.getReturnValue()));
        }
        if (this.getItem() instanceof RangedWeaponItem) {
            cir.setReturnValue(appendRangedTooltip(cir.getReturnValue()));
        }
    }

    @Unique
    public List<Text> appendSwordTooltip(List<Text> list) {
        ElementAttribute attribute;
        try {
            attribute = new ElementAttribute(Objects.requireNonNull(this.getNbt()).getCompound("element_attribute"));
        } catch (RuntimeException e) {
            return list;
        }
        list.add(new TranslatableText("tooltip.element_combating.attack_mode")
                .append(new TranslatableText("mode.element_combating." + attribute.getAttackMode().getAttackMode().getId())));
        list.add(new TranslatableText("tooltip.element_combating.element_type")
                .append(new TranslatableText("effect.element_combating." + attribute.getElementType().getId())));
        list.add(new TranslatableText("tooltip.element_combating.level")
                .append(String.valueOf(attribute.getLevel())));
        return list;
    }

    @Unique
    public List<Text> appendRangedTooltip(List<Text> list) {
        ElementDamageInstance damageInstance;
        try {
            damageInstance = new ElementDamageInstance(Objects.requireNonNull(this.getNbt()).getCompound("projectile_element"));
        } catch (RuntimeException e) {
            return list;
        }
        list.add(new TranslatableText("tooltip.element_combating.element_type")
                .append(new TranslatableText("effect.element_combating." + damageInstance.effect.getId())));
        list.add(new TranslatableText("tooltip.element_combating.level")
                .append(String.valueOf(damageInstance.level)));
        return list;
    }
}
