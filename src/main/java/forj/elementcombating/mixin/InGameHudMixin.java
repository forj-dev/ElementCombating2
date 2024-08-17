package forj.elementcombating.mixin;

import forj.elementcombating.render.BurstIconRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private ItemRenderer itemRenderer;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private BurstIconRenderer renderer;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(MinecraftClient client, CallbackInfo ci) {
        this.renderer = new BurstIconRenderer(this.itemRenderer) {
            private final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

            @Override
            protected void renderIcon(ItemStack item, int x, int y) {
                this.renderer.renderGuiItemIcon(item, x, y);
            }

            @Override
            protected void renderQuad(int x, int y, int w, int h, int r, int g, int b, int alpha) {
                ((QuadRenderAccessor) this.renderer).renderQuad(this.bufferBuilder, x, y, w, h, r, g, b, alpha);
            }
        };
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (!this.client.options.hudHidden) {
            LivingEntity player = this.client.player;
            if (player == null) return;
            this.renderer.render(this.scaledWidth / 2 - 128, this.scaledHeight - 32, 16, player);
        }
    }
}
