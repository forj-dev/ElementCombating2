package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.AttributedAttackMode;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.Utils;
import forj.elementcombating.impl.flags.MagmaEruptAttackFlag;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class MagmaEruptAttackMode extends AttributedAttackMode {
    public MagmaEruptAttackMode(AttributeType attributeType) {
        super(attributeType, "magma_erupt");
    }

    @Override
    protected void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes) {
        int lastTicks = attributes.get("lastTicks").getIntValue();
        double speed = attributes.get("speed").getDoubleValue();
        float knockback = attributes.get("knockback").getFloatValue();
        float width = attributes.get("width").getFloatValue();
        float height = attributes.get("height").getFloatValue();
        int cooldown = attributes.get("cooldown").getIntValue();
        int maxTransferBlocks = attributes.get("maxTransferBlocks").getIntValue();
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                attribute.getElementType(),
                attribute.getLevel(),
                attributes.get("duration").getIntValue(),
                attributes.get("damage").getFloatValue()
        );
        Vec3d motion = Utils.directionVec3d(speed, 0, user.getYaw());
        MagmaEruptAttackFlag flag = new MagmaEruptAttackFlag(lastTicks, motion, user.getPos(), damageInstance, knockback, width, height, maxTransferBlocks);
        ((StatAccessor) user).getFlagManager().add(flag);
        ((StatAccessor) user).getCoolDownManager().set(attribute.getAttributeType(), attribute.getElementType(), cooldown);
    }
}
