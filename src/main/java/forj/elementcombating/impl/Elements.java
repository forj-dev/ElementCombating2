package forj.elementcombating.impl;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.*;
import forj.elementcombating.impl.attack_modes.PulseAttackMode;
import forj.elementcombating.impl.attack_modes.SweepAttackMode;
import forj.elementcombating.impl.entity.entity.ElementCrystalEntity;
import forj.elementcombating.impl.entity.entity.ElementDamageCrystalEntity;
import forj.elementcombating.impl.status_effect.ElementEffects;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.explosion.Explosion;

import java.util.List;

import static forj.elementcombating.element.ElementRegistry.registerElementReaction;

public class Elements {

    //Types

    public static final ElementType Fire = new ElementType("fire", 0xF34418, 0xFF9A9A, true);
    public static final ElementType Water = new ElementType("water", 0x3254FB, 0x7799FD, true);
    public static final ElementType Electricity = new ElementType("electricity", 0x6A0AFB, 0xCB96FD, true);
    public static final ElementType Plant = new ElementType("plant", 0x2EFD3B, 0x8FFF8D, true);
    public static final ElementType Sculk = new ElementType("sculk", 0x235A5A, 0x4A7D7D, true);
    public static final ElementType Soul = new ElementType("soul", 0x60F5FA, 0x9CC7C9, true);
    public static final ElementType Void = new ElementType("void", 0x250031, 0x4B4A4D, true);
    public static final ElementType Stone = new ElementType("stone", 0xD79E2B, 0xCDB077, false);
    public static final ElementType Wind = new ElementType("wind", 0x6FDFBA, 0x8FDFD9, false);

    //Effects

    public static final ElementEffect BurstEffect = new ElementEffect("burst", 0xF16507, true);
    public static final ElementEffect GrowEffect = new ElementEffect("grow", 0x0FFB1E, true);
    public static final ElementEffect ActivateEffect = new ElementEffect("activate", 0x8245DE, true);

    //Reactions

    public static final ElementReaction None = (target, attacker, level, damage) -> {
    };
    public static final ElementReaction Vaporize = (target, attacker, level, damage) -> {
        target.damage(((DamageInfoAccessor) DamageSource.mob(attacker)).setBypassesShield(), damage);
        ParticleS2CPacket packet = new ParticleS2CPacket(ParticleTypes.CLOUD, false, target.getX(), target.getY() + target.getHeight() / 2, target.getZ(), 0.7f, 0.7f, 0.7f, 0.05f, 20);
        for (ServerPlayerEntity player : PlayerLookup.tracking(target))
            player.networkHandler.sendPacket(packet);
    };
    public static final ElementReaction Overload = (target, attacker, level, damage) -> target.getWorld().createExplosion(attacker, target.getX(), target.getY(), target.getZ(), (float) Math.sqrt(level) * 2f, false, Explosion.DestructionType.NONE);
    public static final ElementReaction Burn = (target, attacker, level, damage) -> {
        target.setFireTicks(level * 80);
        target.setAttacker(attacker);
    };
    public static final ElementReaction Burst = (target, attacker, level, damage) -> {
        BurstEffect.attack(target, attacker, level, (int) (Math.sqrt(level) * 200), (float) Math.sqrt(level) * 5f + 0.7f * damage);
        ParticleS2CPacket packet = new ParticleS2CPacket(ParticleTypes.FLAME, false, target.getX(), target.getY() + target.getHeight() / 2, target.getZ(), 0.4f, 0.7f, 0.4f, 0.1f, 10);
        for (ServerPlayerEntity player : PlayerLookup.tracking(target))
            player.networkHandler.sendPacket(packet);
    };
    public static final ElementReaction CrystallizeFire = getCrystallize(Fire);
    public static final ElementReaction DiffuseFire = getDiffuse(Fire, 2.5f);
    public static final ElementReaction Charged = (target, attacker, level, damage) -> {
        target.addStatusEffect(new StatusEffectInstance(ElementEffects.CHARGED_EFFECT, 80, level - 1, false, false, true));
        ElementEffects.CHARGED_EFFECT.applyUpdateEffect(target, level - 1);
    };
    public static final ElementReaction Grow = (target, attacker, level, damage) -> {
        GrowEffect.attack(target, attacker, level, (int) (Math.sqrt(level) * 200), (float) Math.sqrt(level) * 5f + 0.7f * damage);
        ParticleS2CPacket packet = new ParticleS2CPacket(ParticleTypes.HAPPY_VILLAGER, false, target.getX(), target.getY() + target.getHeight() / 2, target.getZ(), 0.4f, 0.7f, 0.4f, 0.1f, 10);
        for (ServerPlayerEntity player : PlayerLookup.tracking(target))
            player.networkHandler.sendPacket(packet);
    };
    public static final ElementReaction CrystallizeWater = getCrystallize(Water);
    public static final ElementReaction DiffuseWater = getDiffuse(Water, 2.5f);
    public static final ElementReaction Activate = (target, attacker, level, damage) -> {
        ActivateEffect.attack(target, attacker, level, (int) (Math.sqrt(level) * 200), (float) Math.sqrt(level) * 5f + 0.7f * damage);
        ParticleS2CPacket packet = new ParticleS2CPacket(new DustParticleEffect(new Vec3f(0.8f,0f,0.1f),ElementCombating.RANDOM.nextFloat(1.5f,2.5f)), false, target.getX(), target.getY() + target.getHeight() / 2, target.getZ(), 0.4f, 0.7f, 0.4f, 0.1f, 10);
        for (ServerPlayerEntity player : PlayerLookup.tracking(target))
            player.networkHandler.sendPacket(packet);
    };
    public static final ElementReaction ShortCircuited = (target, attacker, level, damage) -> {
        target.addStatusEffect(new StatusEffectInstance(ElementEffects.SHORT_CIRCUITED_EFFECT, 80, level - 1, false, false, true));
        ElementEffects.SHORT_CIRCUITED_EFFECT.applyUpdateEffect(target, level - 1);
    };
    public static final ElementReaction CrystallizeElectricity = getCrystallize(Electricity);
    public static final ElementReaction DiffuseElectricity = getDiffuse(Electricity, 2.5f);
    public static final ElementReaction Ignite = getElementDamage(Fire);
    public static final ElementReaction CrystallizePlant = getCrystallize(Plant);
    public static final ElementReaction Poison = (target, attacker, level, damage) -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, ((int) (120 * Math.sqrt(level))), (level == 1 ? 0 : level - 2), false, true, true));
    public static final ElementReaction Levitation = (target, attacker, level, damage) -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 40, level - 1, false, false, true));
    public static final ElementReaction Overflow = (target, attacker, level, damage) -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, ((int) (140 * Math.sqrt(level))), 1, false, false, true));
    public static final ElementReaction Infect = (target, attacker, level, damage) -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, ((int) (50 * Math.sqrt(level))), (int) Math.sqrt(level) - 1, false, true, true));
    public static final ElementReaction Weak = (target, attacker, level, damage) -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, ((int) (80 * Math.sqrt(level))), (level == 1 ? 0 : level - 2), false, true, true));
    public static final ElementReaction Wither = (target, attacker, level, damage) -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, ((int) (60 * Math.sqrt(level))), (level == 1 ? 0 : level - 2), false, true, true));
    public static final ElementReaction Spread = getDiffuse(Plant, 2f);
    public static final ElementReaction SuperBurn = getDiffuse(Fire, 3.5f);
    public static final ElementReaction Record = getElementDamage(Sculk);
    public static final ElementReaction Attract = (target, attacker, level, damage) -> {
        Box box = Box.of(target.getPos(), 5, 5, 5);
        List<LivingEntity> entities = target.getWorld().getEntitiesByClass(LivingEntity.class, box, e -> e.squaredDistanceTo(target) <= 6.25f);
        for (LivingEntity entity : entities) {
            if (target.equals(entity) || attacker.equals(entity)) continue;
            double dx = entity.getX() - target.getX();
            double dy = entity.getY() - target.getY();
            double dz = entity.getZ() - target.getZ();
            double dis = Math.sqrt(dx * dx + dy * dy + dz * dz);
            Vec3d velocity = entity.getVelocity();
            entity.setVelocity(velocity.multiply(0.75).add(-dx / dis, -dy / dis, -dz / dis));
        }
    };
    public static final ElementReaction Syntony = getDiffuse(Sculk, 5f);
    public static final ElementReaction Teleport = (target, attacker, level, damage) -> {
        Vec3d random_motion = new Vec3d(ElementCombating.RANDOM.nextDouble(), ElementCombating.RANDOM.nextDouble(), ElementCombating.RANDOM.nextDouble()).normalize();
        Vec3d pos_motion = attacker.getPos().subtract(target.getPos()).normalize();
        target.setVelocity(target.getVelocity().multiply(0.9).add(pos_motion).add(random_motion.multiply(0.07)));
    };
    public static final ElementReaction Flashover = (target, attacker, level, damage) -> {
        double dmg = 10.0 * level / (level + 1.5);
        ((StatAccessor) target).getShieldManager().attack((float) dmg);
    };
    public static final ElementReaction CrystallizeVoid = getCrystallize(Void);
    public static final ElementReaction DiffuseSoul = getDiffuse(Soul, 2f);

    //Modes
    public static final AttackMode SweepSkillMode = new SweepAttackMode(AttributeType.ITEM_SKILL);
    public static final AttackMode SweepMobMode = new SweepAttackMode(AttributeType.ENTITY_SKILL);
    public static final AttackMode PulseMode = new PulseAttackMode(AttributeType.ENTITY_BURST);


    //Register

    public static void init() {
        ElementRegistry.registerElementTypes(Fire, Water, Electricity, Plant, Sculk, Soul, Void, Stone, Wind);
        ElementRegistry.registerElementEffects(BurstEffect, GrowEffect, ActivateEffect);

        Fire.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Water.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Electricity.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Plant.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Sculk.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Soul.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Void.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Stone.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);
        Wind.addAvailableMode(SweepSkillMode, SweepMobMode, PulseMode);

        ElementRegistry.registerAttackModes(SweepSkillMode, SweepMobMode, PulseMode);


        registerElementReaction(Fire, Water, Vaporize);
        registerElementReaction(Fire, Electricity, Overload);
        registerElementReaction(Fire, Plant, Burn);
        registerElementReaction(Fire, Soul, Burst);
        registerElementReaction(Fire, Void, Poison);
        registerElementReaction(Fire, GrowEffect, SuperBurn);

        registerElementReaction(Water, Fire, Vaporize);
        registerElementReaction(Water, Electricity, Charged);
        registerElementReaction(Water, Plant, Grow);
        registerElementReaction(Water, Soul, Levitation);
        registerElementReaction(Water, ActivateEffect, ShortCircuited);

        registerElementReaction(Electricity, Fire, Overload);
        registerElementReaction(Electricity, Water, Charged);
        registerElementReaction(Electricity, Sculk, Activate);
        registerElementReaction(Electricity, Soul, Overflow);
        registerElementReaction(Electricity, Void, Flashover);
        registerElementReaction(Electricity, GrowEffect, ShortCircuited);

        registerElementReaction(Plant, Fire, Burn);
        registerElementReaction(Plant, Water, Grow);
        registerElementReaction(Plant, Sculk, Spread);
        registerElementReaction(Plant, Soul, Infect);
        registerElementReaction(Plant, Void, Teleport);
        registerElementReaction(Plant, BurstEffect, Ignite);

        registerElementReaction(Sculk, Electricity, Activate);
        registerElementReaction(Sculk, Plant, Spread);
        registerElementReaction(Sculk, Void, Weak);
        registerElementReaction(Sculk, Stone, Syntony);

        registerElementReaction(Soul, Fire, Burst);
        registerElementReaction(Soul, Water, Levitation);
        registerElementReaction(Soul, Electricity, Overflow);
        registerElementReaction(Soul, Plant, Infect);
        registerElementReaction(Soul, Void, Wither);
        registerElementReaction(Soul, Stone, None);
        registerElementReaction(Soul, ActivateEffect, Burst);

        registerElementReaction(Void, Fire, Poison);
        registerElementReaction(Void, Electricity, Flashover);
        registerElementReaction(Void, Plant, Teleport);
        registerElementReaction(Void, Sculk, Weak);
        registerElementReaction(Void, Soul, Wither);
        registerElementReaction(Void, Wind, None);
        registerElementReaction(Void, BurstEffect, Attract);

        registerElementReaction(Stone, Fire, CrystallizeFire);
        registerElementReaction(Stone, Water, CrystallizeWater);
        registerElementReaction(Stone, Electricity, CrystallizeElectricity);
        registerElementReaction(Stone, Sculk, Syntony);
        registerElementReaction(Stone, Soul, None);
        registerElementReaction(Stone, Void, CrystallizeVoid);
        registerElementReaction(Stone, GrowEffect, CrystallizePlant);
        registerElementReaction(Stone, BurstEffect, CrystallizeFire);
        registerElementReaction(Stone, ActivateEffect, Record);

        registerElementReaction(Wind, Fire, DiffuseFire);
        registerElementReaction(Wind, Water, DiffuseWater);
        registerElementReaction(Wind, Electricity, DiffuseElectricity);
        registerElementReaction(Wind, Void, None);
        registerElementReaction(Wind, BurstEffect, DiffuseSoul);
    }

    private static ElementReaction getCrystallize(ElementType type) {
        return (target, attacker, level, damage) -> {
            ElementCrystalEntity entity = new ElementCrystalEntity(target.getWorld(), attacker, 120);
            entity.setPos(target.getX(), target.getY(), target.getZ());
            entity.setVelocity(Utils.randomVec3d(0.2));
            target.getWorld().spawnEntity(entity);
            entity.setElementType(type);
        };
    }

    private static ElementReaction getDiffuse(ElementType type, float range) {
        return (target, attacker, level, damage) -> {
            Box box = Box.of(target.getPos(), 2 * range, 2 * range, 2 * range);
            ParticleS2CPacket packet = new ParticleS2CPacket(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(type.getColor())),ElementCombating.RANDOM.nextFloat(1.5f,2.5f)), false, target.getX(), target.getY() + target.getHeight() / 2, target.getZ(), 0.4f, 0.7f, 0.4f, 0.1f, 20);
            for (ServerPlayerEntity player : PlayerLookup.tracking(target))
                player.networkHandler.sendPacket(packet);
            List<LivingEntity> entities = target.getWorld().getEntitiesByClass(LivingEntity.class, box, e -> e.squaredDistanceTo(target) <= range * range);
            for (LivingEntity entity : entities) {
                if (target.equals(entity) || attacker.equals(entity)) continue;
                type.attack(target, attacker, (level == 1 ? 1 : level - 1), 120, 1);
            }
        };
    }

    private static ElementReaction getElementDamage(ElementType type) {
        return (target, attacker, level, damage) -> {
            ElementDamageCrystalEntity entity = new ElementDamageCrystalEntity(target.getWorld(), attacker, 100);
            entity.setPos(target.getX(), target.getY(), target.getZ());
            entity.setVelocity(Utils.randomVec3d(0.2));
            target.getWorld().spawnEntity(entity);
            entity.setElementType(type);
        };
    }
}
