package forj.elementcombating.element;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;

public class ChargeManager {

    private final Map<ElementType, Float> entries = new HashMap<>();

    public void set(ElementType elementType, float charge) {
        this.entries.put(elementType, charge);
        this.onUpdate(elementType, charge);
    }

    /**
     * Load from NBT.
     */
    public void load(NbtList nbt) {
        this.entries.clear();
        this.onClear();
        for (int i = 0; i < nbt.size(); i++) {
            NbtCompound entry = nbt.getCompound(i);
            this.set(
                    ElementRegistry.getElementTypes().get(entry.getString("type")),
                    entry.getFloat("value")
            );
        }
    }

    /**
     * Save to NBT.
     */
    public NbtList save() {
        NbtList nbt = new NbtList();
        for (Map.Entry<ElementType, Float> entry : this.entries.entrySet()) {
            NbtCompound compound = new NbtCompound();
            compound.putString("type", entry.getKey().getId());
            compound.putFloat("value", entry.getValue());
            nbt.add(compound);
        }
        return nbt;
    }

    /**
     * Sync data to client.
     */
    public void sync() {
        this.onClear();
        for (Map.Entry<ElementType, Float> entry : this.entries.entrySet()) {
            this.onUpdate(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Add charge progress.
     */
    public void charge(ElementType elementType, float charge) {
        float value = this.entries.getOrDefault(elementType, 0f);
        value += charge;
        if (value > 1f)
            value = 1f;
        this.entries.put(elementType, value);
        this.onUpdate(elementType, value);
    }

    /**
     * Get the progress of charging.
     * Range: [0.0, 1.0]
     */
    public float getProgress(ElementType elementType) {
        return this.entries.getOrDefault(elementType, 0f);
    }

    public void remove(ElementType elementType) {
        this.entries.remove(elementType);
        this.onUpdate(elementType, 0f);
    }

    /**
     * Only for data sync.
     */
    public void clear() {
        this.entries.clear();
    }

    protected void onUpdate(ElementType elementType, float charge) {
    }

    protected void onClear() {
    }
}
