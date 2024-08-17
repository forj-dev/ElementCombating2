package forj.elementcombating.mixin;

import forj.elementcombating.render.ElementLabelRenderer;
import forj.elementcombating.render.ElementShieldRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;
    @Unique
    private ElementLabelRenderer labelRenderer;
    @Unique
    private ElementShieldRenderer shieldRenderer;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(EntityRendererFactory.Context ctx, CallbackInfo ci) {
        this.labelRenderer = new ElementLabelRenderer(ctx.getItemRenderer(), this.dispatcher);
        this.shieldRenderer = new ElementShieldRenderer(ctx.getItemRenderer());
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity livingEntity))
            return;
        this.labelRenderer.render(livingEntity, matrices, vertexConsumers);
        this.shieldRenderer.renderShields(livingEntity, tickDelta, matrices, vertexConsumers);
    }
}
