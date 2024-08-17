package forj.elementcombating.item;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class ElementGemUpgradeRecipe extends SpecialCraftingRecipe {
    public static final RecipeSerializer<ElementGemUpgradeRecipe> SERIALIZER = RecipeSerializer.register("crafting_element_gem_upgrade", new SpecialRecipeSerializer<>(ElementGemUpgradeRecipe::new));

    public ElementGemUpgradeRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        int count = 0, level = -1;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() != Items.ELEMENT_GEM) return false;
            NbtCompound nbt = stack.getNbt();
            if (nbt == null) return false;
            int lvl = nbt.getInt("level");
            if (level == -1) level = lvl;
            if (lvl != level) return false;
            count++;
        }
        return count == 9;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        int count = 0, level = -1;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() != Items.ELEMENT_GEM) return ItemStack.EMPTY;
            NbtCompound nbt = stack.getNbt();
            if (nbt == null) return ItemStack.EMPTY;
            int lvl = nbt.getInt("level");
            if (level == -1) level = lvl;
            if (lvl != level) return ItemStack.EMPTY;
            count++;
        }
        if (count != 9) return ItemStack.EMPTY;
        ItemStack out = new ItemStack(Items.ELEMENT_GEM);
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("level", level + 1);
        out.setNbt(nbt);
        return out;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }
}
