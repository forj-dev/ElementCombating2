package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.AttributedAttackMode;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.flags.ChainLightningAttackerFlag;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public class ChainLightningAttackMode extends AttributedAttackMode {
    public ChainLightningAttackMode(AttributeType attributeType) {
        super(attributeType, "chain_lightning");
    }

    @Override
    protected void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes) {
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                attribute.getElementType(),
                attribute.getLevel(),
                attributes.get("duration").getIntValue(),
                attributes.get("damage").getFloatValue()
        );
        ChainLightningAttackerFlag flag = new ChainLightningAttackerFlag(
                attributes.get("spreadInterval").getIntValue(),
                attributes.get("range").getFloatValue(),
                damageInstance, user,
                attributes.get("maxTargets").getIntValue(),
                attributes.get("spreadTimes").getIntValue()
        );
        ((StatAccessor) user).getFlagManager().add(flag);
        ((StatAccessor) user).getCoolDownManager().set(
                attribute.getAttributeType(), attribute.getElementType(),
                attributes.get("cooldown").getIntValue()
        );
    }
}
