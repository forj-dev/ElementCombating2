package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;

import java.util.List;

public class DefaultAttackMode extends AttackMode {
    public DefaultAttackMode(AttributeType attributeType) {
        super(attributeType, "default");
    }

    @Override
    public NbtCompound create(ElementType type) {
        float charge_speed = ElementCombating.RANDOM.nextFloat(0.1f, 0.25f);
        float attack_range = ElementCombating.RANDOM.nextFloat(2f, 2.5f) - charge_speed;
        NbtCompound nbt = new NbtCompound();
        nbt.putFloat("charge", charge_speed);
        nbt.putFloat("range", attack_range);
        return nbt;
    }

    @Override
    public boolean verify(NbtCompound modeData) {
        float s = modeData.getFloat("charge");
        float r = modeData.getFloat("range");
        return s >= 0.1f && s <= 0.25f && r >= 1.75f && r <= 2.5f;
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute) {
        NbtCompound nbt = attribute.getAttackData();
        float charge_speed = nbt.getFloat("charge");
        float attack_range = nbt.getFloat("range");
        int cd = Math.max(0, (int) (charge_speed * 40));
        StatAccessor accessor = (StatAccessor) user;
        accessor.getCoolDownManager().set(attribute.getAttributeType(), attribute.getElementType(), cd);
        accessor.getChargeManager().charge(attribute.getElementType(), charge_speed);

        boolean bl = user instanceof HostileEntity;

        List<LivingEntity> entities = user.getWorld().getEntitiesByClass(LivingEntity.class, new Box(user.getX() - attack_range, user.getY() - attack_range, user.getZ() - attack_range, user.getX() + attack_range, user.getY() + attack_range, user.getZ() + attack_range), entity -> true);
        for (LivingEntity entity : entities) {
            if (entity.equals(user) || (bl && entity instanceof HostileEntity)) continue;
            attribute.getElementType().attack(entity, user, attribute.getLevel(), 200, attribute.getLevel() * 4);
            entity.takeKnockback(0.5f, user.getX() - entity.getX(), user.getZ() - entity.getZ());
        }
    }
}
