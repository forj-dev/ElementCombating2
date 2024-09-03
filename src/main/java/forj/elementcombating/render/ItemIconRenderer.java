package forj.elementcombating.render;

import com.mojang.blaze3d.systems.RenderSystem;
import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.ElementDamageInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;

import java.util.Objects;


public abstract class ItemIconRenderer {

    protected abstract void renderQuad(int x, int y, int height, int red, int green, int blue, int alpha);

    public void render(ItemStack stack, int x, int y) {
        tryRenderSword(stack, x, y);
        tryRenderRanged(stack, x, y);
    }

    public void tryRenderSword(ItemStack stack, int x, int y) {
        if (!(stack.getItem() instanceof SwordItem)) return;
        ElementAttribute attribute;
        try {
            attribute = new ElementAttribute(Objects.requireNonNull(stack.getNbt()).getCompound("element_attribute"));
        } catch (RuntimeException e) {
            return;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        float progress = player == null ? 0f : ((StatAccessor) player).getCoolDownManager()
                .getProgress(AttributeType.ITEM_SKILL, attribute.getElementType());
        int color = attribute.getElementType().getColor();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        //element icon
        this.renderQuad(x, y, 2, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 191);
        //cd progress
        if (progress > 0f) {
            int i = Math.round(progress * 16f);
            this.renderQuad(x + 14, y, i, 64, 32, 0, 127);//progress background
            this.renderQuad(x + 14, y + i, 16 - i, 8, 8, 255, 127);//progress bar
        }
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }

    public void tryRenderRanged(ItemStack stack, int x, int y) {
        if (!(stack.getItem() instanceof RangedWeaponItem)) return;
        ElementDamageInstance damageInstance;
        try {
            damageInstance = new ElementDamageInstance(Objects.requireNonNull(stack.getNbt()).getCompound("projectile_element"));
        } catch (RuntimeException e) {
            return;
        }
        int color = damageInstance.effect.getColor();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        //element icon
        this.renderQuad(x, y, 2, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 191);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }
}
