package forj.elementcombating.element;

import forj.elementcombating.ElementCombating;

import java.util.ArrayList;

public class ElementProvider {
    private ArrayList<Float> possibilities;

    ElementProvider(float... possibilities) {
        this.possibilities = normalize(possibilities);
    }

    private ArrayList<Float> normalize(float... possibilities) {
        ArrayList<Float> normalized = new ArrayList<>();
        float sum = 0;
        for (float possibility : possibilities) {
            sum += possibility;
        }
        for (int i = 0; i < ElementRegistry.getElementTypes().size(); i++) {
            if (i < possibilities.length) {
                if (sum > 0.0f)
                    normalized.add(possibilities[i] / sum);
                else
                    normalized.add(0.0f);
            } else {
                normalized.add(0.0f);
            }
        }
        return normalized;
    }

    @SuppressWarnings("unused")
    public void setPossibilities(float... possibilities) {
        this.possibilities = normalize(possibilities);
    }

    private int randomIndex() {
        float random = (float) Math.random();
        float sum = 0.0f;
        for (int i = 0; i < possibilities.size(); i++) {
            sum += possibilities.get(i);
            if (sum >= random)
                return i;
        }
        return -1;
    }

    public ElementType nextElement() {
        int index = randomIndex();
        if (index == -1)
            index = ElementCombating.RANDOM.nextInt(ElementRegistry.getElementTypes().size());
        return ElementRegistry.getElementTypes().get(index);
    }

}
