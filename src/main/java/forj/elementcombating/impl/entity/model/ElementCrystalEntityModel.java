package forj.elementcombating.impl.entity.model;

import forj.elementcombating.impl.entity.entity.ElementCrystalEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ElementCrystalEntityModel extends AnimatedGeoModel<ElementCrystalEntity> {
    @Override
    public Identifier getModelLocation(ElementCrystalEntity object) {
        return new Identifier("element_combating", "geo/element_crystal.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ElementCrystalEntity object) {
        return new Identifier("element_combating", "textures/entity/element_crystal_" + object.getElementType() + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(ElementCrystalEntity animatable) {
        return new Identifier("element_combating", "animations/element_crystal.animation.json");
    }
}
