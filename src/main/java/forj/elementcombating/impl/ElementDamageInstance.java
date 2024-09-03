package forj.elementcombating.impl;

import forj.elementcombating.element.ElementEffect;
import forj.elementcombating.element.ElementRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class ElementDamageInstance {
    public ElementEffect effect;
    public int level, duration;
    public float damage;

    public ElementDamageInstance(ElementEffect effect, int level, int duration, float damage) {
        this.effect = effect;
        this.level = level;
        this.duration = duration;
        this.damage = damage;
    }

    /**
     * Load from NBT.
     */
    public ElementDamageInstance(NbtCompound nbt) {
        this.effect = ElementRegistry.getElementEffect(nbt.getString("type"));
        this.level = nbt.getInt("level");
        this.duration = nbt.getInt("duration");
        this.damage = nbt.getFloat("damage");
        if (this.effect == null || this.level < 1 || this.duration < 1 || this.damage < 0)
            throw new IllegalArgumentException("Invalid element damage instance data.");
    }

    /**
     * Save to NBT.
     */
    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("type", effect.getId());
        nbt.putInt("level", level);
        nbt.putInt("duration", duration);
        nbt.putFloat("damage", damage);
        return nbt;
    }

    public void damage(LivingEntity target) {
        effect.attack(target, target, level, duration, damage);
    }

    public void damage(LivingEntity target, LivingEntity attacker) {
        effect.attack(target, attacker, level, duration, damage);
    }

}
