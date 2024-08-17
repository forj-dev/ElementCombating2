package forj.elementcombating.impl.entity.renderer;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

import java.util.Collections;

public class GeoEntityRenderer<T extends Entity & IAnimatable> extends GeoProjectilesRenderer<T> {
    private final AnimatedGeoModel<T> modelProvider;

    public static <F extends Entity & IAnimatable> EntityRendererFactory<F> getRendererFactory(AnimatedGeoModel<F> model) {
        return ctx -> new GeoEntityRenderer<>(ctx, model);
    }

    public GeoEntityRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<T> modelProvider) {
        super(ctx, modelProvider);
        this.modelProvider = modelProvider;
    }

    @Override
    public RenderLayer getRenderType(T animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
                       VertexConsumerProvider bufferIn, int packedLightIn) {
        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entityIn));
        matrixStackIn.push();
        matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entityYaw));
        MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entityIn));
        Color renderColor = getRenderColor(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(entityIn, partialTicks, matrixStackIn, bufferIn, null, packedLightIn,
                getTexture(entityIn));
        render(model, entityIn, partialTicks, renderType, matrixStackIn, bufferIn, null, packedLightIn,
                getPackedOverlay(entityIn, 0), (float) renderColor.getRed() / 255f,
                (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
                (float) renderColor.getAlpha() / 255);

        float lastLimbDistance = 0.0F;
        float limbSwing = 0.0F;
        EntityModelData entityModelData = new EntityModelData();
        AnimationEvent<T> predicate = new AnimationEvent<>(entityIn, limbSwing, lastLimbDistance, partialTicks,
                false, Collections.singletonList(entityModelData));
        ((IAnimatableModel<T>) modelProvider).setLivingAnimations(entityIn, this.getUniqueID(entityIn), predicate);
        matrixStackIn.pop();
        if (this.hasLabel(entityIn))
            this.renderLabelIfPresent(entityIn, entityIn.getDisplayName(), matrixStackIn, bufferIn, packedLightIn);
    }
}
