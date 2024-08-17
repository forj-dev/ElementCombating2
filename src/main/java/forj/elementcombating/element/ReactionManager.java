package forj.elementcombating.element;

import java.util.ArrayList;
import java.util.List;

public class ReactionManager {
    private ListId currentList = ListId.List1;
    private final List<ElementReactionInstance> list1 = new ArrayList<>();
    private final List<ElementReactionInstance> list2 = new ArrayList<>();

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void update() {
        currentList = currentList.reverse();
        if (currentList == ListId.List1) {
            for (int i = 0; i < list1.size(); i++) {
                list1.get(i).react();
            }
            list1.clear();
        } else {
            for (int i = 0; i < list2.size(); i++) {
                list2.get(i).react();
            }
            list2.clear();
        }
    }

    public void scheduleReaction(ElementReactionInstance reaction) {
        if (currentList == ListId.List1) {
            list1.add(reaction);
        } else {
            list2.add(reaction);
        }
    }

    private enum ListId {
        List1, List2;

        ListId reverse() {
            if (this == List1)
                return List2;
            return List1;
        }
    }
}
