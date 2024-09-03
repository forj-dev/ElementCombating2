package forj.elementcombating.mixin;

import forj.elementcombating.element.ElementRegistry;
import forj.elementcombating.impl.ElementDamageInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PillagerEntity.class)
public abstract class PillagerEntityMixin extends MobEntity {
    protected PillagerEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initEquipment", at = @At("HEAD"), cancellable = true)
    public void initEquipment(LocalDifficulty difficulty, CallbackInfo ci) {
        if (Math.random() <= difficulty.getClampedLocalDifficulty() / 2.0 +0.5){
            ItemStack stack = new ItemStack(Items.CROSSBOW);
            ElementDamageInstance instance = new ElementDamageInstance(
                    ElementRegistry.randomElement(),
                    1, 100, 1f
            );
            stack.setSubNbt("projectile_element",instance.save());
            this.equipStack(EquipmentSlot.MAINHAND, stack);
            ci.cancel();
        }
    }
}
