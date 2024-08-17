package forj.elementcombating.render;

import forj.elementcombating.element.ElementEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.Collection;

public class ElementLabelRenderer {

    private static final float SCALE = 0.4f;
    private static final float MAX_DIS = 16f;
    private static final float HEIGHT = 0.3f;


    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher dispatcher;

    public ElementLabelRenderer(ItemRenderer itemRenderer, EntityRenderDispatcher dispatcher) {
        this.itemRenderer = itemRenderer;
        this.dispatcher = dispatcher;
    }

    @SuppressWarnings("DataFlowIssue")
    public void render(LivingEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (this.dispatcher.getSquaredDistanceToCamera(entity) > MAX_DIS * MAX_DIS)
            return;
        Collection<ElementEffect> effects = new ArrayList<>();
        for (StatusEffectInstance effect : entity.getStatusEffects()) {
            if (!(effect.getEffectType() instanceof ElementEffect elementEffect))
                continue;
            effects.add(elementEffect);
        }
        float offset = (effects.size() - 1) * SCALE / 2f;
        double radian = Math.toRadians(MinecraftClient.getInstance().player.getYaw());
        double x = Math.cos(radian);
        double z = Math.sin(radian);
        for (ElementEffect effect : effects) {
            matrices.push();
            matrices.translate(offset * x, entity.getHeight() + HEIGHT, offset * z);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(SCALE, SCALE, SCALE);
            this.itemRenderer.renderItem(effect.iconItem(), ModelTransformation.Mode.GUI, 0xF000F0, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getId());
            matrices.pop();
            offset -= SCALE;
        }
    }
}
