package forj.elementcombating.item;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static forj.elementcombating.item.Items.ELEMENT_CRYSTAL;

@SuppressWarnings("unused")
public class ElementGemCraftRecipe extends SpecialCraftingRecipe {
    public static final RecipeSerializer<ElementGemCraftRecipe> SERIALIZER = RecipeSerializer.register("crafting_element_gem", new SpecialRecipeSerializer<>(ElementGemCraftRecipe::new));

    public ElementGemCraftRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        final Item[] recipe = {
                ELEMENT_CRYSTAL, ELEMENT_CRYSTAL, ELEMENT_CRYSTAL,
                ELEMENT_CRYSTAL, net.minecraft.item.Items.EMERALD, ELEMENT_CRYSTAL,
                ELEMENT_CRYSTAL, ELEMENT_CRYSTAL, ELEMENT_CRYSTAL
        };
        for (int i = 0; i < 9; i++) {
            if (!recipe[i].equals(inventory.getStack(i).getItem()))
                return false;
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        return Items.ELEMENT_GEM.getDefaultStack();
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }
}
