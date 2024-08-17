package forj.elementcombating.impl.status_effect;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ElementEffects {
    public static final ChargedEffect CHARGED_EFFECT = new ChargedEffect();
    public static final ShortCircuitedEffect SHORT_CIRCUITED_EFFECT = new ShortCircuitedEffect();

    public static void register() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier("element_combating", "charged_effect"), CHARGED_EFFECT);
        Registry.register(Registry.STATUS_EFFECT, new Identifier("element_combating", "short_circuited_effect"), SHORT_CIRCUITED_EFFECT);
    }
}
