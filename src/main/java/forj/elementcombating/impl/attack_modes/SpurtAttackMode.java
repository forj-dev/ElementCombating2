package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.AttributedAttackMode;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.Utils;
import forj.elementcombating.impl.flags.SpurtAttackFlag;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class SpurtAttackMode extends AttributedAttackMode {
    public SpurtAttackMode(AttributeType attributeType) {
        super(attributeType, "spurt");
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes) {
        int lastTicks = attributes.get("lastTicks").getIntValue();
        int speedUpTicks = attributes.get("speedUpTicks").getIntValue();
        double speedUpStrength = attributes.get("speedUpStrength").getDoubleValue();
        float removeSpeed = attributes.get("removeSpeed").getFloatValue();
        int duration = attributes.get("duration").getIntValue();
        float damage = attributes.get("damage").getFloatValue();
        int knockback = attributes.get("knockback").getIntValue();
        int cooldown = attributes.get("cooldown").getIntValue();
        Vec3d motion = Utils.directionVec3d(speedUpStrength, user.getPitch(), user.getYaw());
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                attribute.getElementType(),
                attribute.getLevel(),
                duration,
                damage
        );
        ((StatAccessor) user).getCoolDownManager().set(attribute.getAttributeType(), attribute.getElementType(), cooldown);
        SpurtAttackFlag flag = new SpurtAttackFlag(lastTicks, speedUpTicks, motion, removeSpeed, damageInstance, knockback);
        ((StatAccessor) user).getFlagManager().add(flag);
    }
}
