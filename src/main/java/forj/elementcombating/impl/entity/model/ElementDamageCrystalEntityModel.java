package forj.elementcombating.impl.entity.model;

import forj.elementcombating.impl.entity.entity.ElementDamageCrystalEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ElementDamageCrystalEntityModel extends AnimatedGeoModel<ElementDamageCrystalEntity> {
    @Override
    public Identifier getModelLocation(ElementDamageCrystalEntity object) {
        return new Identifier("element_combating", "geo/element_crystal.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ElementDamageCrystalEntity object) {
        return new Identifier("element_combating", "textures/entity/element_damage_crystal_" + object.getElementType() + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(ElementDamageCrystalEntity animatable) {
        return new Identifier("element_combating", "animations/element_crystal.animation.json");
    }
}
