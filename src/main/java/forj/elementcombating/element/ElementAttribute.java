package forj.elementcombating.element;

import forj.elementcombating.ElementCombating;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

/**
 * Attribute of an element attack.
 */
public class ElementAttribute {
    private final AttributeType attributeType;
    private final ElementType elementType;
    private final AttackModeInstance attackModeInstance;
    private int level;

    /**
     * Generate an attribute randomly.
     *
     * @param attributeType Type of generation
     * @param level         Element level
     */
    public ElementAttribute(AttributeType attributeType, int level, ElementProvider provider) {
        this.attributeType = attributeType;
        this.level = level;
        this.elementType = provider.nextElement();
        List<AttackMode> modes = this.elementType.getAvailableModes(attributeType);
        if (!modes.isEmpty())
            this.attackModeInstance = new AttackModeInstance(
                    modes.get(ElementCombating.RANDOM.nextInt(modes.size())), this.elementType);
        else
            this.attackModeInstance = new AttackModeInstance(
                    EmptyMode.Instance.get(attributeType), this.elementType);
    }

    public ElementAttribute(AttributeType attributeType, ElementType elementType, AttackMode attackMode, int level) {
        this.attributeType = attributeType;
        this.level = level;
        this.elementType = elementType;
        this.attackModeInstance = new AttackModeInstance(attackMode, elementType);
    }

    /**
     * Load attribute from NBT.
     *
     * @param nbt NbtCompound that contains attribute data
     * @throws RuntimeException Failed to load (NBT format incorrect)
     */
    public ElementAttribute(NbtCompound nbt) throws RuntimeException {
        try {
            this.attributeType = AttributeType.valueOf(nbt.getString("attribute_type"));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to load from NBT.");
        }
        try {
            this.elementType = ElementRegistry.getElementTypes().get(nbt.getString("element_type"));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to load from NBT.");
        }
        this.level = nbt.getInt("level");
        if (this.level < 0) throw new RuntimeException("Failed to load from NBT.");
        this.attackModeInstance = new AttackModeInstance(nbt.getCompound("attack_mode"));
    }

    /**
     * Save to an NBT.
     */
    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("attribute_type", this.attributeType.toString());
        nbt.putString("element_type", this.elementType.getId());
        nbt.putInt("level", this.level);
        nbt.put("attack_mode", this.attackModeInstance.save());
        return nbt;
    }

    /**
     * Let an entity use this element attack.
     *
     * @param user Element attack user
     */
    public void use(LivingEntity user) {
        this.attackModeInstance.getAttackMode().onUse(user, this);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public NbtCompound getAttackData() {
        return attackModeInstance.getData();
    }

    public AttackModeInstance getAttackMode() {
        return attackModeInstance;
    }
}
