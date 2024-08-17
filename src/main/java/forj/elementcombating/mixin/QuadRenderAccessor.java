package forj.elementcombating.mixin;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemRenderer.class)
public interface QuadRenderAccessor {
    @Invoker("renderGuiQuad")
    void renderQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);
}
