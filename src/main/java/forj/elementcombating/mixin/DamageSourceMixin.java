package forj.elementcombating.mixin;

import forj.elementcombating.element.DamageInfoAccessor;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public abstract class DamageSourceMixin implements DamageInfoAccessor {
    @Unique
    private boolean bypassShield = false;

    @Unique
    @Override
    public DamageSource setBypassesShield() {
        this.bypassShield = true;
        return (DamageSource) (Object) this;
    }

    @Unique
    @Override
    public boolean bypassesShield() {
        return this.bypassShield;
    }
}
