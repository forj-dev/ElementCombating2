package forj.elementcombating.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Items {
    public static final Item ELEMENT_CRYSTAL = new Item(new Item.Settings().group(ItemGroup.MISC));
    public static final Item ELEMENT_GEM = new ElementGemItem();

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("element_combating", "element_crystal"), ELEMENT_CRYSTAL);
        Registry.register(Registry.ITEM, new Identifier("element_combating", "element_gem"), ELEMENT_GEM);
    }
}
