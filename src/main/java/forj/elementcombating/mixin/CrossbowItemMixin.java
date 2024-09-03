package forj.elementcombating.mixin;

import forj.elementcombating.element.ArrowElementAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci, boolean bl, ProjectileEntity projectileEntity){
        try {
            @SuppressWarnings("DataFlowIssue")
            ElementDamageInstance instance = new ElementDamageInstance(
                    crossbow.getNbt().getCompound("projectile_element")
            );
            if (projectileEntity instanceof ArrowEntity) {
                ((ArrowElementAccessor) projectileEntity).setElementDamageInstance(instance);
            }
        } catch (RuntimeException ignored) {
        }
    }
}
