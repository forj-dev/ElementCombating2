package forj.elementcombating.render;

import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.ShieldManager;
import forj.elementcombating.element.StatAccessor;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.Map;

public class ElementShieldRenderer {
    private final ItemRenderer itemRenderer;

    public ElementShieldRenderer(ItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
    }

    public void renderShields(LivingEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        ShieldManager manager = ((StatAccessor) entity).getShieldManager();
        if (manager.crystalShieldType != null) {
            float tickRotation = entity.age * manager.crystalShieldEntry.spinSpeed;
            float rotation = MathHelper.lerp(tickDelta, tickRotation, tickRotation + manager.crystalShieldEntry.spinSpeed);
            renderShield(entity, manager.crystalShieldType, rotation, 1.9f, matrices, vertexConsumers);
        }
        float size = 1.7f;
        for (Map.Entry<ElementType, ShieldManager.Entry> skillShield : manager.skillShield.entrySet()) {
            float tickRotation = entity.age * skillShield.getValue().spinSpeed;
            float rotation = MathHelper.lerp(tickDelta, tickRotation, tickRotation + skillShield.getValue().spinSpeed);
            renderShield(entity, skillShield.getKey(), rotation, size, matrices, vertexConsumers);
            size -= 0.2f;
        }
    }

    public void renderShield(LivingEntity entity, ElementType type, float rotation, float size, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();

        matrices.scale(entity.getWidth(), entity.getHeight(), entity.getWidth());
        float rSqrtSize = MathHelper.fastInverseSqrt(size);
        matrices.scale(size, 0.9f / rSqrtSize, size);
        matrices.translate(0, -0.05f / rSqrtSize, 0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));

        ItemStack itemStack = type.getShieldIcon();
        Quaternion rotate90 = Vec3f.POSITIVE_Y.getDegreesQuaternion(90);

        for (int i = 0; i < 4; i++) {
            matrices.push();
            matrices.translate(0, 0.5, 0.5);
            matrices.scale(1, 1, 0);
            this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.NONE, 0xF000F0, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getId());
            matrices.pop();
            matrices.multiply(rotate90);
        }

        matrices.pop();
    }
}
