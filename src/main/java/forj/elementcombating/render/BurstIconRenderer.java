package forj.elementcombating.render;

import com.mojang.blaze3d.systems.RenderSystem;
import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.StatAccessor;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public abstract class BurstIconRenderer {
    protected final ItemRenderer renderer;

    public BurstIconRenderer(ItemRenderer itemRenderer) {
        this.renderer = itemRenderer;
    }

    protected abstract void renderIcon(ItemStack item, int x, int y);

    protected abstract void renderQuad(int x, int y, int w, int h, int r, int g, int b, int alpha);

    public void render(int x, int y, int width, LivingEntity player) {
        StatAccessor accessor = (StatAccessor) player;
        ElementAttribute attribute = accessor.getBurstAttribute();
        if (attribute.getLevel() == 0)
            return;
        ElementType type = attribute.getElementType();
        float charge = accessor.getChargeManager().getProgress(type);
        float cd = accessor.getCoolDownManager().getProgress(AttributeType.ENTITY_BURST, type);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        renderQuad(x, y, 1, width + 2, 16, 16, 16, 80);
        renderQuad(x + 1, y, width, 1, 16, 16, 16, 80);
        renderQuad(x + 1, y + width + 1, width, 1, 16, 16, 16, 80);
        renderQuad(x + width + 1, y, 1, width + 2, 16, 16, 16, 80);
        if (charge >= 1f && cd == 0f) { //activate
            int color = type.getColor();
            renderQuad(x + 1, y + 1, width, width, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 90);
        } else { //inactivate
            int color = type.getInactivateColor();
            int charge_h = Math.round(charge * width);
            renderQuad(x + 1, y + 1, width, 16 - charge_h, 128, 128, 128, 85);
            renderQuad(x + 1, y + width + 1 - charge_h, width, charge_h, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 85);
        }
        renderIcon(type.iconItem(), x + 1, y + 1);
        if (cd != 0f) {
            int cd_w = Math.round(cd * width);
            renderQuad(x + 1, y + width - 1, cd_w, 2, 16, 128, 255, 95);
            renderQuad(x + 1 + cd_w, y + width - 1, width - cd_w, 2, 16, 16, 16, 95);
        }
    }
}
