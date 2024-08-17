package forj.elementcombating.element;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public abstract class AttackMode {
    private final AttributeType attributeType;

    private final String id;

    protected AttackMode(AttributeType attributeType, String id) {
        this.attributeType = attributeType;
        this.id = id;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public String getId() {
        return id;
    }

    /**
     * Create some random data for a new item or entity.
     *
     * @param type The {@link ElementType} chosen by {@link ElementAttribute}
     * @return The NBT format of data
     */
    public abstract NbtCompound create(ElementType type);

    /**
     * Check if the given data is valid.
     *
     * @param modeData Attack mode data
     */
    public abstract boolean verify(NbtCompound modeData);

    /**
     * Called when an entity uses the element attack.
     *
     * @param attribute The attribute of the element attack
     */
    public abstract void onUse(LivingEntity user, ElementAttribute attribute);

    @Override
    public String toString() {
        return this.id;
    }
}
