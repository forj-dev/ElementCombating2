package forj.elementcombating.render;

import forj.elementcombating.element.ElementAttribute;
import forj.elementcombating.element.StatAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class BurstAttributeTextRenderer {
    public static void renderAttributeText(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        TextRenderer textRenderer = client.textRenderer;
        ElementAttribute attribute = ((StatAccessor) client.player).getBurstAttribute();
        if (attribute.getLevel() == 0) return;
        int color = 0x9f000000 | attribute.getElementType().getColor();
        textRenderer.draw(matrices, new TranslatableText("tooltip.element_combating.burst_attribute"), 5, 5, color);
        textRenderer.draw(matrices, new TranslatableText("tooltip.element_combating.element_type")
                        .append(new TranslatableText("effect.element_combating." +
                                attribute.getElementType()))
                , 5, 15, color);
        textRenderer.draw(matrices, new TranslatableText("tooltip.element_combating.level")
                        .append(String.valueOf(attribute.getLevel()))
                , 5, 25, color);
        textRenderer.draw(matrices, new TranslatableText("tooltip.element_combating.attack_mode")
                        .append(new TranslatableText("mode.element_combating." +
                                attribute.getAttackMode().getAttackMode().getId()))
                , 5, 35, color);
    }
}
