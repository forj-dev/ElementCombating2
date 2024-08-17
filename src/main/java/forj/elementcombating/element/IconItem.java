package forj.elementcombating.element;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class IconItem extends Item {
    private final ElementEffect elementEffect;

    public IconItem(ElementEffect elementEffect) {
        super(new Settings());
        this.elementEffect = elementEffect;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        this.elementEffect.apply(user, user, 1, 200, 0f);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
