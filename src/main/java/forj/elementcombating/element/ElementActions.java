package forj.elementcombating.element;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class ElementActions {
    /**
     * Use the skill of the item. (Players only)
     */
    public static ActionResult itemSkill(PlayerEntity user, ItemStack item) {
        ElementAttribute attribute;
        try {
            if (item.getNbt() != null) {
                attribute = new ElementAttribute(item.getNbt().getCompound("element_attribute"));
            } else return ActionResult.PASS;
        } catch (RuntimeException e) {
            return ActionResult.PASS;
        }
        if (((StatAccessor) user).getCoolDownManager()
                .getProgress(AttributeType.ITEM_SKILL, attribute.getElementType()) > 0)
            return ActionResult.PASS;

        if (user.getServer() != null) attribute.use(user);

        return ActionResult.SUCCESS;
    }

    /**
     * Use the skill of the entity. (Server side only)
     */
    public static boolean entitySkill(LivingEntity user) {
        ElementAttribute attribute = ((StatAccessor) user).getSkillAttribute();
        if (((StatAccessor) user).getCoolDownManager()
                .getProgress(AttributeType.ENTITY_SKILL, attribute.getElementType()) > 0)
            return false;
        attribute.use(user);
        return true;
    }

    /**
     * Use the burst of the entity. (Server side only)
     */
    public static boolean entityBurst(LivingEntity user) {
        ElementAttribute attribute = ((StatAccessor) user).getBurstAttribute();
        if (attribute.getLevel() == 0)
            return false;
        if (((StatAccessor) user).getCoolDownManager()
                .getProgress(AttributeType.ENTITY_BURST, attribute.getElementType()) > 0)
            return false;
        if (((StatAccessor) user).getChargeManager().getProgress(attribute.getElementType()) < 1f)
            return false;
        ((StatAccessor) user).getChargeManager().remove(attribute.getElementType());
        attribute.use(user);
        return true;
    }

}
