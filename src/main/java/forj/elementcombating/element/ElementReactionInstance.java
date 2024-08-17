package forj.elementcombating.element;

import net.minecraft.entity.LivingEntity;

public class ElementReactionInstance {

    private final ElementReaction reaction;
    private final LivingEntity target;
    private final LivingEntity attacker;
    private final int level;
    private final float damage;

    public ElementReactionInstance(ElementReaction reaction, LivingEntity target, LivingEntity attacker, int level, float damage) {
        this.reaction = reaction;
        this.target = target;
        this.attacker = attacker;
        this.level = level;
        this.damage = damage;
    }

    /**
     * Run {@link ElementReaction} with the parameters.
     */
    public void react() {
        if (this.target == null || this.target.isDead() || this.target.isRemoved())
            return;
        if (this.attacker == null || this.attacker.isDead() || this.attacker.isRemoved())
            this.reaction.react(this.target, this.target, this.level, this.damage);
        else
            this.reaction.react(this.target, this.attacker, this.level, this.damage);
    }
}
