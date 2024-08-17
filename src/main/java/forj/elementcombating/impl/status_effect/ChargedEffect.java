package forj.elementcombating.impl.status_effect;

import forj.elementcombating.impl.Elements;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.Box;

import java.util.List;

public class ChargedEffect extends StatusEffect {
    public ChargedEffect() {
        super(StatusEffectCategory.HARMFUL, 0x3476DA);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 20 == 1;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        Box box = Box.of(entity.getPos(), 10, 10, 10);
        List<LivingEntity> targets = entity.getWorld().getEntitiesByClass(LivingEntity.class, box, e -> e.squaredDistanceTo(entity) <= 25);
        for (LivingEntity target : targets) {
            if (entity.equals(target) || target.hasStatusEffect(Elements.Water)) {
                LivingEntity attacker = entity.getAttacker();
                if (attacker == null)
                    attacker = entity;
                Elements.Electricity.attack(target, attacker, amplifier, 40, 0.5f);
            }
        }
    }
}
