package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.*;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.Utils;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Map;

public class ShieldBashAttackMode extends AttributedAttackMode {
    public ShieldBashAttackMode(AttributeType attributeType) {
        super(attributeType, "shield_bash");
    }

    @Override
    protected void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes) {
        int shieldDuration = attributes.get("shieldDuration").getIntValue();
        int durability = attributes.get("durability").getIntValue();
        int attackElementDuration = attributes.get("attackElementDuration").getIntValue();
        float damage = attributes.get("damage").getFloatValue();
        float range = attributes.get("range").getFloatValue();
        float knockback = attributes.get("knockback").getFloatValue();
        int cooldown = attributes.get("cooldown").getIntValue();
        ((StatAccessor) user).getCoolDownManager().set(attribute.getAttributeType(), attribute.getElementType(), cooldown);
        ((StatAccessor) user).getShieldManager().set(true, attribute.getElementType(), shieldDuration, durability);
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                attribute.getElementType(),
                attribute.getLevel(),
                attackElementDuration,
                damage
        );
        Box box = Box.of(user.getPos(), range * 2, range * 2, range * 2);
        float squaredRange = range * range;
        for (LivingEntity target : user.world.getEntitiesByClass(LivingEntity.class, box, (entity) -> true)) {
            if (target == user || target.squaredDistanceTo(user) > squaredRange) continue;
            damageInstance.damage(target, user);
            Utils.knockback(target, user, knockback);
        }
        //spawn a particle sphere
        int particleCount = (int) ((range + 1) * (range + 1));
        final double angleMultiplier = 2 * Math.PI / particleCount;
        for (int i = 0; i < particleCount; i++) {
            double y = user.getY() + user.getHeight() / 2 + Math.sin(i * angleMultiplier) * range;
            for (int j = 0; j < particleCount; j++) {
                double x = user.getX() + Math.cos(j * angleMultiplier) * Math.cos(i * angleMultiplier) * range;
                double z = user.getZ() + Math.sin(j * angleMultiplier) * Math.cos(i * angleMultiplier) * range;
                Utils.spawnParticle((ServerWorld) user.getWorld(), new DustParticleEffect(
                        new Vec3f(Vec3d.unpackRgb(damageInstance.effect.getColor())),
                        ElementCombating.RANDOM.nextFloat(1f, 2f)
                ), x, y, z);
            }
        }
    }
}
