package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.element.*;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.flags.PulseAttackFlag;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public class PulseAttackMode extends AttributedAttackMode {

    public PulseAttackMode(AttributeType attributeType) {
        super(attributeType, "pulse");
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes) {
        int times = attributes.get("times").getIntValue();
        float damage = attributes.get("damage").getFloatValue();
        float range = attributes.get("range").getFloatValue();
        int cooldown = attributes.get("cooldown").getIntValue();
        int pulseInterval = attributes.get("pulse_interval").getIntValue();
        ElementType type = attribute.getElementType();
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                type,
                attribute.getLevel(),
                150,
                damage
        );
        StatAccessor accessor = (StatAccessor) user;
        accessor.getCoolDownManager().set(attribute.getAttributeType(), type, cooldown);
        PulseAttackFlag flag = new PulseAttackFlag(times, pulseInterval, range, damageInstance);
        accessor.getFlagManager().add(flag);
    }
}
