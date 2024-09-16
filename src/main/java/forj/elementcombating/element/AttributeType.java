package forj.elementcombating.element;

/**
 * Types of element attacks.
 */
public enum AttributeType {

    ITEM_SKILL(0, "item_skill"),
    ENTITY_SKILL(1, "entity_skill"),
    ENTITY_BURST(2, "entity_burst");

    public static final AttributeType[] typeList = {ITEM_SKILL, ENTITY_SKILL, ENTITY_BURST};

    private final int index;
    private final String id;

    AttributeType(int index, String id) {
        this.index = index;
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }
}
