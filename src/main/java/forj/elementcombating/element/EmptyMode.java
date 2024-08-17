package forj.elementcombating.element;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;

public class EmptyMode extends AttackMode {
    public static final Map<AttributeType, EmptyMode> Instance = new HashMap<>();

    static {
        Instance.put(AttributeType.ITEM_SKILL, new EmptyMode(AttributeType.ITEM_SKILL));
        Instance.put(AttributeType.ENTITY_SKILL, new EmptyMode(AttributeType.ENTITY_SKILL));
        Instance.put(AttributeType.ENTITY_BURST, new EmptyMode(AttributeType.ENTITY_BURST));
    }

    public EmptyMode(AttributeType attributeType) {
        super(attributeType, "empty");
    }

    @Override
    public NbtCompound create(ElementType type) {
        return new NbtCompound();
    }

    @Override
    public boolean verify(NbtCompound modeData) {
        return true;
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute) {
    }
}
