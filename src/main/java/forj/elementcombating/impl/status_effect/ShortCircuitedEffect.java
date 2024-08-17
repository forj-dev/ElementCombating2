package forj.elementcombating.impl.status_effect;

import forj.elementcombating.impl.Elements;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ShortCircuitedEffect extends StatusEffect {
    public ShortCircuitedEffect() {
        super(StatusEffectCategory.HARMFUL, 0x3F31FD);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 15 == 1;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        Elements.Electricity.attack(entity, entity, amplifier, 20, 0.5f);
    }
}
