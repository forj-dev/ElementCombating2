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
    public void onUse(LivingEntity user, ElementAttribute attribute) {
        Map<String, AttributeCreator.Num> attributes = apply(attribute);
        int times = (int) attributes.get("times").getLongValue();
        float damage = (float) attributes.get("damage").getDoubleValue();
        float range = (float) attributes.get("range").getDoubleValue();
        int cooldown = (int) attributes.get("cooldown").getLongValue();
        int pulseInterval = (int) attributes.get("pulse_interval").getLongValue();
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
