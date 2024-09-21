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
    public void onUse(LivingEntity user, ElementAttribute attribute) {
        Map<String, AttributeCreator.Num> attributes = apply(attribute);
        int lastTicks = (int) attributes.get("lastTicks").getLongValue();
        int speedUpTicks = (int) attributes.get("speedUpTicks").getLongValue();
        double speedUpStrength = attributes.get("speedUpStrength").getDoubleValue();
        float removeSpeed = (float) attributes.get("removeSpeed").getDoubleValue();
        int duration = (int) attributes.get("duration").getLongValue();
        float damage = (float) attributes.get("damage").getDoubleValue();
        int knockback = (int) attributes.get("knockback").getLongValue();
        int cooldown = (int) attributes.get("cooldown").getLongValue();
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
