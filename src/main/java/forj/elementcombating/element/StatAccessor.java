package forj.elementcombating.element;

import forj.elementcombating.impl.ElementDamageInstance;

public interface StatAccessor {

    ChargeManager getChargeManager();

    CoolDownManager getCoolDownManager();

    ShieldManager getShieldManager();

    FlagManager getFlagManager();

    void scheduleReaction(ElementReactionInstance reaction);

    ElementAttribute getSkillAttribute();

    ElementAttribute getBurstAttribute();

    ElementDamageInstance getProjectileElement();

    @SuppressWarnings("unused")
    void setSkillAttribute(ElementAttribute attribute);

    void setBurstAttribute(ElementAttribute attribute);

    @SuppressWarnings("unused")
    void setProjectileElement(ElementDamageInstance element);

}
