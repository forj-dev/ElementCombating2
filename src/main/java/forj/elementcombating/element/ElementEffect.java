package forj.elementcombating.element;

import forj.elementcombating.ElementCombating;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.Map;

public class ElementEffect extends StatusEffect {

    private final String id;
    private final ItemStack iconItem; //The icon of the effect
    private final boolean attachable; //Controls whether this effect can be added to an entity or not

    /**
     * @param color      Used in {@link ElementType}
     * @param attachable Controls whether this effect can be added to an entity or not
     */
    public ElementEffect(String id, int color, boolean attachable) {
        super(StatusEffectCategory.NEUTRAL, color);
        this.id = id;
        IconItem icon = new IconItem(this);
        this.iconItem = new ItemStack(icon);
        this.attachable = attachable;
        Registry.register(Registry.ITEM, new Identifier("element_combating", "element_" + id), icon);
    }

    /**
     * @return ItemStack of the icon
     */
    public ItemStack iconItem() {
        return this.iconItem;
    }

    public String getId() {
        return id;
    }

    public static void syncEffect(LivingEntity target, ElementEffect effect, int duration) {
        if (!(target.getWorld() instanceof ServerWorld)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(target.getId());
        buf.writeString(effect.getId());
        buf.writeVarInt(duration);
        for (ServerPlayerEntity player : PlayerLookup.tracking(target)) {
            ServerPlayNetworking.send(player, ElementCombating.EffectSync, buf);
        }
    }

    /**
     * React to another element effect on an entity.
     *
     * @return true - Reaction succeed --- false - There is no available effects
     */
    public boolean tryReact(LivingEntity target, LivingEntity attacker, int level, float damage) {
        Collection<StatusEffectInstance> effects = target.getStatusEffects();
        Map<ElementEffect, ElementReaction> reactionMap = ElementRegistry.getReactions(this);
        for (StatusEffectInstance effect : effects) {
            if (!(effect.getEffectType() instanceof ElementEffect effectType)) continue;
            if (!reactionMap.containsKey(effectType)) continue;
            int reactionLevel = Math.min(level, effect.getAmplifier() + 1);
            if (reactionLevel <= 0) continue;
            ((StatAccessor) target).scheduleReaction(new ElementReactionInstance(
                    reactionMap.get(effectType),
                    target,
                    attacker,
                    level,
                    damage
            ));
            syncEffect(target, effectType, 0);
            target.removeStatusEffect(effectType);
            return true;
        }
        return false;
    }

    /**
     * Try to react to another element effect on an entity.&nbsp<p>
     * If failed, add the effect to the entity.
     */
    public void apply(LivingEntity target, LivingEntity attacker, int level, int duration, float damage) {
        if (this.tryReact(target, attacker, level, damage)) return;
        if (!this.attachable) return;
        syncEffect(target, this, duration);
        target.addStatusEffect(
                new StatusEffectInstance(this, duration, level - 1, false, false),
                attacker);
    }

    public void attack(LivingEntity target, LivingEntity attacker, int level, int duration, float damage) {
        if (this instanceof ElementType)
            damage = ((StatAccessor) target).getShieldManager().attack((ElementType) this, damage);
        else
            damage = ((StatAccessor) target).getShieldManager().attack(damage);


        if (damage > 0f) {
            if (target.isAttackable())
                target.damage(((DamageInfoAccessor) DamageSource.mob(attacker)).setBypassesShield(), damage);
            if (target.isAlive())
                this.apply(target, attacker, level, duration, damage);
        }
    }

    @Override
    public String toString() {
        return this.id;
    }
}