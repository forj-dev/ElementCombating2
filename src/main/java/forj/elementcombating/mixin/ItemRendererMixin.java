package forj.elementcombating.mixin;

import forj.elementcombating.render.ItemIconRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    @SuppressWarnings("SameParameterValue")
    protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Unique
    private final ItemIconRenderer renderer = new ItemIconRenderer() {
        private final BufferBuilder builder = Tessellator.getInstance().getBuffer();

        @Override
        protected void renderQuad(int x, int y, int height, int red, int green, int blue, int alpha) {
            renderGuiQuad(builder, x, y, 2, height, red, green, blue, alpha);
        }
    };

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;<init>()V"))
    public void render(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        this.renderer.render(stack, x, y);
    }
}
