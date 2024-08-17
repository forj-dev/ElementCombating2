package forj.elementcombating.impl.entity;

import forj.elementcombating.impl.entity.entity.ElementCrystalEntity;
import forj.elementcombating.impl.entity.entity.ElementDamageCrystalEntity;
import forj.elementcombating.impl.entity.model.ElementDamageCrystalEntityModel;
import forj.elementcombating.impl.entity.model.ElementCrystalEntityModel;
import forj.elementcombating.impl.entity.renderer.GeoEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Entities {
    public static final EntityType<ElementCrystalEntity> ELEMENT_CRYSTAL_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier("element_combating", "element_crystal"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<ElementCrystalEntity>) ElementCrystalEntity::new).dimensions(EntityDimensions.fixed(0.375f, 0.8125f)).build());
    public static final EntityType<ElementDamageCrystalEntity> ELEMENT_DAMAGE_CRYSTAL_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier("element_combating", "recorder"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<ElementDamageCrystalEntity>) ElementDamageCrystalEntity::new).dimensions(EntityDimensions.fixed(0.375f, 0.8125f)).build());

    public static void init() {
    }

    public static void initClient() {
        EntityRendererRegistry.register(ELEMENT_CRYSTAL_ENTITY, GeoEntityRenderer.getRendererFactory(new ElementCrystalEntityModel()));
        EntityRendererRegistry.register(ELEMENT_DAMAGE_CRYSTAL_ENTITY, GeoEntityRenderer.getRendererFactory(new ElementDamageCrystalEntityModel()));
    }
}
