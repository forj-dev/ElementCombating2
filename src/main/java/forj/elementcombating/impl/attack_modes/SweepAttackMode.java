package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.*;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.Elements;
import forj.elementcombating.impl.flags.SweepAttackFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class SweepAttackMode extends AttackMode {
    public SweepAttackMode(AttributeType attributeType) {
        super(attributeType, "sweep");
    }

    @Override
    public NbtCompound create(ElementType type) {
        int tickAddition = 0;
        if (type == Elements.Wind || type == Elements.Soul)
            tickAddition = 3;
        int lastTicks = ElementCombating.RANDOM.nextInt(10, 16);
        float damage = ElementCombating.RANDOM.nextInt(6) / 2f + 3f;
        int duration = (20 - lastTicks) * 10 + (int) ((6 - damage) * 20);
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("lastTicks", lastTicks + tickAddition);
        nbt.putFloat("damage", damage);
        nbt.putInt("duration", duration);
        return nbt;
    }

    @Override
    public boolean verify(NbtCompound modeData) {
        int lastTicks = modeData.getInt("lastTicks");
        float damage = modeData.getFloat("damage");
        int duration = modeData.getInt("duration");
        boolean bl1 = lastTicks >= 10 && lastTicks <= 18;
        boolean bl2 = damage >= 3f && damage <= 6f;
        boolean bl3 = duration >= 30 && duration <= 160;
        return bl1 && bl2 && bl3;
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute) {
        NbtCompound modeData = attribute.getAttackData();
        int lastTicks = modeData.getInt("lastTicks");
        float damage = modeData.getFloat("damage");
        int duration = modeData.getInt("duration");
        ElementType type = attribute.getElementType();
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                type,
                attribute.getLevel(),
                duration,
                damage * (float) Math.sqrt(attribute.getLevel())
        );
        StatAccessor accessor = (StatAccessor) user;
        accessor.getCoolDownManager().set(attribute.getAttributeType(), attribute.getElementType(), 50 + lastTicks);
        float hitBoxSize = 0.75f, hitBoxDistance = 1.75f;
        if (type == Elements.Void || type == Elements.Stone) {
            hitBoxSize = 0.7f;
            hitBoxDistance = 1.85f;
        }
        float pitchSpeed = ElementCombating.RANDOM.nextFloat(2f) - 1f;
        float yawSpeed = (float) Math.sqrt(160 - pitchSpeed * pitchSpeed);
        if (ElementCombating.RANDOM.nextBoolean())
            yawSpeed = -yawSpeed;
        float pitch = -pitchSpeed * lastTicks / 2;
        float yaw = -yawSpeed * lastTicks / 2;
        final float speedModifier = 1.9f;
        SweepAttackFlag flag = new SweepAttackFlag((int) (lastTicks / speedModifier), pitch, yaw, pitchSpeed * speedModifier, yawSpeed * speedModifier, hitBoxSize, hitBoxDistance, damageInstance, user);
        ((StatAccessor) user).getFlagManager().add(flag);
    }
}
