package forj.elementcombating.element;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.utils.MapList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ElementRegistry {

    private static final Map<String, ElementEffect> elementEffects = new HashMap<>();

    private static final MapList<String, ElementType> elementTypes = new MapList<>();

    private static final Map<ElementEffect, Map<ElementEffect, ElementReaction>> reactions = new HashMap<>();

    private static final Map<String, AttackMode> attackModes = new HashMap<>();


    public static void registerElementEffect(ElementEffect effect) {
        Registry.register(
                Registry.STATUS_EFFECT,
                new Identifier("element_combating", effect.getId()),
                effect
        );
        reactions.put(effect, new HashMap<>());
        elementEffects.put(effect.getId(), effect);
    }

    public static void registerElementType(ElementType type) {
        registerElementEffect(type);
        elementTypes.put(type.getId(), type);
    }

    public static void registerElementEffects(ElementEffect... effects) {
        for (ElementEffect effect : effects) {
            registerElementEffect(effect);
        }
    }

    public static void registerElementTypes(ElementType... types) {
        for (ElementType type : types) {
            registerElementType(type);
        }
    }

    public static void registerElementReaction(ElementEffect triggerEffect, ElementEffect conditionEffect, ElementReaction reaction) {
        reactions.get(triggerEffect).put(conditionEffect, reaction);
    }

    public static void registerAttackMode(AttackMode mode) {
        attackModes.put(mode.getId(), mode);
    }

    public static void registerAttackModes(AttackMode... modes) {
        for (AttackMode mode : modes)
            registerAttackMode(mode);
    }

    public static MapList<String, ElementType> getElementTypes() {
        return elementTypes;
    }

    public static ElementEffect getElementEffect(String id) {
        return elementEffects.get(id);
    }

    public static Map<ElementEffect, ElementReaction> getReactions(ElementEffect effect) {
        return reactions.get(effect);
    }

    public static AttackMode getAttackMode(String id) {
        if ("empty".equals(id))
            return EmptyMode.Instance.get(AttributeType.ITEM_SKILL);
        return attackModes.get(id);
    }

    public static Collection<AttackMode> getAttackModes() {
        return attackModes.values();
    }

    public static ElementType randomElement() {
        return elementTypes.get(ElementCombating.RANDOM.nextInt(elementTypes.size()));
    }
}
