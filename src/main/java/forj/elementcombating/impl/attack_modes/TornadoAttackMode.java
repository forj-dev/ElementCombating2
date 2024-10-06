package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.AttributedAttackMode;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.entity.entity.TornadoEntity;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import net.minecraft.entity.LivingEntity;

import java.util.Map;

public class TornadoAttackMode extends AttributedAttackMode {
    public TornadoAttackMode(AttributeType attributeType) {
        super(attributeType, "tornado");
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes) {
        int lastTicks = attributes.get("lastTicks").getIntValue();
        int duration = attributes.get("duration").getIntValue();
        float damage = attributes.get("damage").getFloatValue();
        int damageInterval = attributes.get("damageInterval").getIntValue();
        float range = attributes.get("range").getFloatValue();
        double speed = attributes.get("speed").getDoubleValue();
        int cooldown = attributes.get("cooldown").getIntValue();
        ((StatAccessor)user).getCoolDownManager()
                .set(attribute.getAttributeType(), attribute.getElementType(), cooldown);
        TornadoEntity tornado = new TornadoEntity(user.getWorld(), user, lastTicks, damageInterval,
                duration, attribute.getLevel(), damage, range);
        double yawRadians = Math.toRadians(-user.getYaw());
        double velocityX = speed * Math.sin(yawRadians);
        double velocityZ = speed * Math.cos(yawRadians);
        tornado.setVelocity(velocityX, 0, velocityZ);
        tornado.setPos(user.getX(), user.getY(), user.getZ());
        user.getWorld().spawnEntity(tornado);
        tornado.setElementType(attribute.getElementType());
    }
}
