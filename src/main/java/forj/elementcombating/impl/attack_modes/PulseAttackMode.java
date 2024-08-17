package forj.elementcombating.impl.attack_modes;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.*;
import forj.elementcombating.impl.ElementDamageInstance;
import forj.elementcombating.impl.Elements;
import forj.elementcombating.impl.flags.PulseAttackFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class PulseAttackMode extends AttackMode {
    public PulseAttackMode(AttributeType attributeType) {
        super(attributeType, "pulse");
    }

    @Override
    public NbtCompound create(ElementType type) {
        float damageAddition = 0f, rangeAddition = 0f;
        if (type == Elements.Electricity || type == Elements.Sculk)
            damageAddition = 0.8f;
        if (type == Elements.Wind || type == Elements.Soul)
            rangeAddition = 0.5f;
        int times = ElementCombating.RANDOM.nextInt(3, 6);
        float damage = ElementCombating.RANDOM.nextFloat(4f, 6.5f);
        float range = (20f - times - damage) * 0.5f - 1f;
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("times", times);
        nbt.putFloat("damage", damage + damageAddition);
        nbt.putFloat("range", range + rangeAddition);
        return nbt;
    }

    @Override
    public boolean verify(NbtCompound modeData) {
        int times = modeData.getInt("times");
        float damage = modeData.getFloat("damage");
        float range = modeData.getFloat("range");
        boolean bl1 = times >= 3 && times <= 5;
        boolean bl2 = damage >= 4f && damage <= 7.3f;
        boolean bl3 = range >= 3.25f && range <= 6f;
        return bl1 && bl2 && bl3;
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute) {
        NbtCompound modeData = attribute.getAttackData();
        int times = modeData.getInt("times");
        float damage = modeData.getFloat("damage");
        float range = modeData.getFloat("range");
        int pulseInterval = (int) (100f / (times + 1));
        ElementType type = attribute.getElementType();
        ElementDamageInstance damageInstance = new ElementDamageInstance(
                type,
                attribute.getLevel(),
                150,
                damage * (float) Math.sqrt(attribute.getLevel())
        );
        StatAccessor accessor = (StatAccessor) user;
        accessor.getCoolDownManager().set(attribute.getAttributeType(), type, times * pulseInterval + 60);
        PulseAttackFlag flag = new PulseAttackFlag(times, pulseInterval, range, damageInstance);
        accessor.getFlagManager().add(flag);
    }
}
