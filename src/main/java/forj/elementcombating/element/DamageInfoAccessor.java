package forj.elementcombating.element;

import net.minecraft.entity.damage.DamageSource;

public interface DamageInfoAccessor {
    DamageSource setBypassesShield();

    boolean bypassesShield();
}
