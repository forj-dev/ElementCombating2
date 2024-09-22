package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.AttributedAttackMode;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.flags.SweepAttackFlag;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public class SweepAttackMode extends AttributedAttackMode {
    public SweepAttackMode(AttributeType attributeType) {
        super(attributeType, "sweep");
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes) {
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                attribute.getElementType(),
                attribute.getLevel(),
                attributes.get("duration").getIntValue(),
                attributes.get("damage").getFloatValue()
        );
        StatAccessor accessor = (StatAccessor) user;
        accessor.getCoolDownManager().set(attribute.getAttributeType(), attribute.getElementType(),
                attributes.get("cooldown").getIntValue());
        SweepAttackFlag flag = new SweepAttackFlag(
                attributes.get("lastTicks").getIntValue(),
                attributes.get("pitch").getFloatValue(),
                attributes.get("yaw").getFloatValue(),
                attributes.get("pitchSpeed").getFloatValue(),
                attributes.get("yawSpeed").getFloatValue(),
                attributes.get("hitBoxSize").getFloatValue(),
                attributes.get("hitBoxDistance").getFloatValue(),
                damageInstance,
                user
        );
        accessor.getFlagManager().add(flag);
    }
}
