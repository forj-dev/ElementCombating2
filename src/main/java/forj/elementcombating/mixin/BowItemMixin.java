package forj.elementcombating.mixin;

import forj.elementcombating.element.ArrowElementAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        if (f < 1F) return;
        try {
            @SuppressWarnings("DataFlowIssue")
            ElementDamageInstance instance = new ElementDamageInstance(
                    stack.getNbt().getCompound("projectile_element")
            );
            if (persistentProjectileEntity instanceof ArrowEntity) {
                ((ArrowElementAccessor) persistentProjectileEntity).setElementDamageInstance(instance);
            }
        } catch (RuntimeException ignored) {
        }
    }
}
