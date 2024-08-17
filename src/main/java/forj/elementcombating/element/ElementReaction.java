package forj.elementcombating.element;

import net.minecraft.entity.LivingEntity;

public interface ElementReaction {
    /**
     * Called when an element reaction happened.
     *
     * @param target   The reaction entity
     * @param attacker The entity that triggered the reaction
     * @param level    Reaction level
     */
    void react(LivingEntity target, LivingEntity attacker, int level, float damage);
}
