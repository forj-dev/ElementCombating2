package forj.elementcombating.element;

/**
 * Types of element attacks.
 */
public enum AttributeType {

    ITEM_SKILL(0),
    ENTITY_SKILL(1),
    ENTITY_BURST(2);

    public static final AttributeType[] typeList = {ITEM_SKILL, ENTITY_SKILL, ENTITY_BURST};

    private final int index;

    AttributeType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
