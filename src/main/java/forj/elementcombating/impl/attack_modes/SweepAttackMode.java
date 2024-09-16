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
    public void onUse(LivingEntity user, ElementAttribute attribute) {
        Map<String, AttributeCreator.Num> attributes = apply(attribute);
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                attribute.getElementType(),
                attribute.getLevel(),
                (int) attributes.get("duration").getLongValue(),
                (float) attributes.get("damage").getDoubleValue()
        );
        StatAccessor accessor = (StatAccessor) user;
        accessor.getCoolDownManager().set(attribute.getAttributeType(), attribute.getElementType(),
                (int) attributes.get("cooldown").getLongValue());
        SweepAttackFlag flag = new SweepAttackFlag(
                (int) attributes.get("lastTicks").getLongValue(),
                (float) attributes.get("pitch").getDoubleValue(),
                (float) attributes.get("yaw").getDoubleValue(),
                (float) attributes.get("pitchSpeed").getDoubleValue(),
                (float) attributes.get("yawSpeed").getDoubleValue(),
                (float) attributes.get("hitBoxSize").getDoubleValue(),
                (float) attributes.get("hitBoxDistance").getDoubleValue(),
                damageInstance,
                user
        );
        accessor.getFlagManager().add(flag);
    }
}
