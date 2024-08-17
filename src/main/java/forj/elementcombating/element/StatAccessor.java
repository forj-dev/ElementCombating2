package forj.elementcombating.element;

public interface StatAccessor {

    ChargeManager getChargeManager();

    CoolDownManager getCoolDownManager();

    ShieldManager getShieldManager();

    FlagManager getFlagManager();

    void scheduleReaction(ElementReactionInstance reaction);

    ElementAttribute getSkillAttribute();

    ElementAttribute getBurstAttribute();

    @SuppressWarnings("unused")
    void setSkillAttribute(ElementAttribute attribute);

    void setBurstAttribute(ElementAttribute attribute);

}
