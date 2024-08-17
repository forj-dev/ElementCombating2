package forj.elementcombating.item;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElementGemItem extends Item {
    private final ItemStack defaultStack = new ItemStack(this);


    public ElementGemItem() {
        super(new Settings().group(ItemGroup.MISC));
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("level", 1);
        defaultStack.setNbt(nbt);
    }

    private void syncAttribute(PlayerEntity user) {
        if (!(user instanceof ServerPlayerEntity player)) return;
        NbtCompound nbt = ((StatAccessor) player).getBurstAttribute().save();
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeNbt(nbt);
        ServerPlayNetworking.send(player, ElementCombating.AttributeSync, buffer);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack item = user.getStackInHand(hand);
        if (hand != Hand.MAIN_HAND) return TypedActionResult.pass(item);
        NbtCompound nbt = item.getNbt();
        if (nbt == null) return TypedActionResult.pass(item);
        int gem_lv = nbt.getInt("level");
        ElementAttribute attribute = ((StatAccessor) user).getBurstAttribute();
        int user_lv = attribute.getLevel();
        if (user_lv == 0 && gem_lv == 1) {
            if(!user.isCreative())item.decrement(1);
            ((StatAccessor) user).setBurstAttribute(new ElementAttribute(AttributeType.ENTITY_BURST, 1));
            syncAttribute(user);
            return TypedActionResult.success(item);
        }
        if (gem_lv == user_lv + 1) {
            if(!user.isCreative())item.decrement(1);
            attribute.setLevel(gem_lv);
            syncAttribute(user);
            return TypedActionResult.success(item);
        }
        if (gem_lv == user_lv - 1) {
            if(!user.isCreative())item.decrement(1);
            attribute.setLevel(0);
            syncAttribute(user);
            return TypedActionResult.success(item);
        }
        return TypedActionResult.pass(item);
    }

    @Override
    public ItemStack getDefaultStack() {
        return defaultStack.copy();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return Rarity.COMMON;
        int level = nbt.getInt("level");
        if (level <= 0) return Rarity.COMMON;
        if (level < 3) return Rarity.UNCOMMON;
        if (level < 5) return Rarity.RARE;
        return Rarity.EPIC;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;
        int level = nbt.getInt("level");
        tooltip.add(new TranslatableText("tooltip.element_combating.level").append(String.valueOf(level)));
    }
}
