package forj.elementcombating.element;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementType extends ElementEffect {

    //Color of ready(charged and not cooling down) element burst icon
    private final int inactivate_color;

    //Icon of element shield
    private final ItemStack shield_icon;

    //(For random generation of ElementAttribute)
    private final Map<AttributeType, List<AttackMode>> available_modes = new HashMap<>();

    /**
     * Icon item should be registered in {@link net.minecraft.util.registry.Registry}.
     *
     * @param id               Identifier in NBT
     * @param color            Color of ready burst icon & item icon
     * @param inactivate_color Color of unready burst icon
     * @param attachable       Controls whether this element can be added to an entity or not
     */
    public ElementType(String id, int color, int inactivate_color, boolean attachable) {
        super(id, color, attachable);
        this.inactivate_color = inactivate_color;
        for (AttributeType type : AttributeType.values()) {
            this.available_modes.put(type, new ArrayList<>());
        }
        Item shieldIcon = new Item(new Item.Settings());
        this.shield_icon = new ItemStack(shieldIcon);
        Registry.register(Registry.ITEM, new Identifier("element_combating", "element_shield_" + id), shieldIcon);
    }

    /**
     * Make the {@link AttackMode}(s) available in this ElementType.
     *
     * @param modes The {@link AttackMode}(s)
     */
    public void addAvailableMode(AttackMode... modes) {
        for (AttackMode mode : modes) {
            this.available_modes.get(mode.getAttributeType()).add(mode);
        }
    }

    public List<AttackMode> getAvailableModes(AttributeType type) {
        return this.available_modes.get(type);
    }

    public int getInactivateColor() {
        return inactivate_color;
    }

    public ItemStack getShieldIcon() {
        return shield_icon;
    }
}
