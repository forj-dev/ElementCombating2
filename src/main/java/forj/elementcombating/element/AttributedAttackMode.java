package forj.elementcombating.element;

import forj.elementcombating.DatapackProcessor;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import forj.elementcombating.utils.attribute_creator.AttributeCreatorProvider;
import forj.elementcombating.utils.attribute_creator.MultiMap;
import forj.elementcombating.utils.attribute_creator.NbtMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;

public abstract class AttributedAttackMode extends AttackMode {
    protected AttributeCreator creator, validator, applicator;

    protected AttributedAttackMode(AttributeType attributeType, String id) {
        super(attributeType, id);
        DatapackProcessor.registerOnReload(this::onReload);
    }

    private void onReload() {
        AttributeCreatorProvider provider = AttributeCreatorProvider.Instance;
        creator = provider.getCreator(id + "_mode_creator");
        validator = provider.getCreator(id + "_mode_validator");
        applicator = provider.getCreator(id + "_mode_applicator");
    }

    @Override
    public NbtCompound create(ElementType type) {
        return creator.createNbt(new AttributedElementMap(attributeType, type, 1));
    }

    @Override
    public boolean verify(NbtCompound modeData) {
        return validator.create(modeData).getOrDefault("valid", new AttributeCreator.Num(false))
                .getBooleanValue();
    }

    @Override
    public void onUse(LivingEntity user, ElementAttribute attribute) {
        Map<String, AttributeCreator.Num> attributes = apply(attribute);
        onUse(user, attribute, attributes);
    }

    protected abstract void onUse(LivingEntity user, ElementAttribute attribute, Map<String, AttributeCreator.Num> attributes);

    protected Map<String, AttributeCreator.Num> apply(ElementAttribute attribute){
        AttributedElementMap map = new AttributedElementMap(attributeType, attribute.getElementType(), attribute.getLevel());
        return applicator.create(new MultiMap(map, new NbtMap(attribute.getAttackData())));
    }

    static class AttributedElementMap extends HashMap<String, AttributeCreator.Num> {
        private final String attributeTypeId, elementTypeId;
        private final int level;

        public AttributedElementMap(AttributeType attributeType, ElementType elementType, int level) {
            attributeTypeId = "is_"+attributeType.getId();
            elementTypeId = "is_"+elementType.getId();
            this.level = level;
        }

        @Override
        public AttributeCreator.Num get(Object key) {
            String id = (String) key;
            if ("level".equals(id))
                return new AttributeCreator.Num(level);
            if (id.equals(attributeTypeId)||id.equals(elementTypeId))
                return new AttributeCreator.Num(true);
            return null;
        }
    }
}
