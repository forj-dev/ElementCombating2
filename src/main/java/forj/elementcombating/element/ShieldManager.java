package forj.elementcombating.element;

import forj.elementcombating.ElementCombating;

import java.util.*;

public class ShieldManager {

    public ElementType crystalShieldType = null;
    public Entry crystalShieldEntry = null;
    public final Map<ElementType, Entry> skillShield = new HashMap<>();

    public void update() {
        if (crystalShieldEntry != null && !crystalShieldEntry.update()) {
            this.onUpdate(false, crystalShieldType, 0, 0);
            crystalShieldType = null;
            crystalShieldEntry = null;
        }
        if (!skillShield.isEmpty()) {
            Iterator<Map.Entry<ElementType, Entry>> iterator = skillShield.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ElementType, Entry> entry = iterator.next();
                if (entry.getValue().update()) continue;
                this.onUpdate(true, entry.getKey(), 0, 0);
                iterator.remove();
            }
        }
    }

    public void set(boolean fromSkill, ElementType elementType, int duration, int health) {
        if (fromSkill) {
            skillShield.put(elementType, new Entry(duration, health));
        } else {
            crystalShieldType = elementType;
            crystalShieldEntry = new Entry(duration, health);
        }
        this.onUpdate(fromSkill, elementType, duration, health);
    }

    private float getDamageMultiplier(ElementType shieldType, ElementType attackType) {
        if (attackType == null)
            return 1f;
        return ElementRegistry.getReactions(attackType).containsKey(shieldType) ? 2f : 1f;
    }

    /**
     * Attack on shields with element.
     *
     * @param elementType ElementType of attack
     * @param damage      Attack damage
     * @return Damage remains, or 0f if the shields defends the attack
     */
    public float attack(ElementType elementType, float damage) {
        if (crystalShieldType != null) {
            if (crystalShieldType == elementType)
                return 0f;

            float f = damage;
            damage -= crystalShieldEntry.health;
            if (crystalShieldEntry.attack(getDamageMultiplier(crystalShieldType, elementType) * f))
                this.onUpdate(false, crystalShieldType, crystalShieldEntry.tick, crystalShieldEntry.health);
            else
                this.remove(false, crystalShieldType);
            if (damage <= 0) return 0f;
        }

        List<ElementType> toRemove = new ArrayList<>();
        for (Map.Entry<ElementType, Entry> shield : skillShield.entrySet()) {
            if (shield.getKey() == elementType)
                return 0f;
            float f = damage;
            damage -= shield.getValue().health;
            if (shield.getValue().attack(getDamageMultiplier(shield.getKey(), elementType) * f))
                this.onUpdate(true, shield.getKey(), shield.getValue().tick, shield.getValue().health);
            else
                toRemove.add(shield.getKey());
            if (damage <= 0) return 0f;
        }
        for (ElementType type : toRemove)
            this.remove(true, type);

        return damage;
    }

    /**
     * Attack on shields without element.
     *
     * @param damage Attack damage
     * @return Damage remains, or 0f if the shields defends the attack
     */
    public float attack(float damage) {
        return this.attack(null, damage);
    }

    /**
     * Only for {@link forj.elementcombating.element.network.S2CPacketReceiver}
     */
    public void remove(boolean fromSkill, ElementType elementType) {
        if (fromSkill) {
            skillShield.remove(elementType);
        } else {
            crystalShieldType = null;
            crystalShieldEntry = null;
        }
        this.onUpdate(fromSkill, elementType, 0, 0);
    }

    protected void onUpdate(boolean fromSkill, ElementType elementType, int duration, int health) {
    }

    public static class Entry {
        int tick;
        int health;
        public float spinSpeed;

        boolean update() {
            tick--;
            return tick > 0;
        }

        boolean attack(float damage) {
            this.health -= (int) damage;
            return this.health > 0;
        }

        Entry(int tick, int health) {
            this.tick = tick;
            this.health = health;
            this.spinSpeed = (ElementCombating.RANDOM.nextFloat() - 0.5f) * 5;
            if (Math.abs(this.spinSpeed) < 1f)
                this.spinSpeed = this.spinSpeed > 0 ? 1 : -1;
        }
    }
}
