package forj.elementcombating.impl.entity.model;

import forj.elementcombating.impl.entity.entity.TornadoEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TornadoEntityModel extends AnimatedGeoModel<TornadoEntity> {
    @Override
    public Identifier getModelLocation(TornadoEntity object) {
        return new Identifier("element_combating", "geo/tornado.geo.json");
    }

    @Override
    public Identifier getTextureLocation(TornadoEntity object) {
        return new Identifier("element_combating", "textures/entity/tornado_" + object.getElementType() + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(TornadoEntity animatable) {
        return new Identifier("element_combating", "animations/tornado.animation.json");
    }
}
