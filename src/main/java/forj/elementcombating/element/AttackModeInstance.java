package forj.elementcombating.element;

import net.minecraft.nbt.NbtCompound;

public class AttackModeInstance {

    private final AttackMode attackMode;

    private final NbtCompound data;

    /**
     * Generate an instance randomly.
     */
    public AttackModeInstance(AttackMode attackMode, ElementType type) {
        this.attackMode = attackMode;
        this.data = attackMode.create(type);
    }

    /**
     * Load from an NBT.
     *
     * @throws RuntimeException Failed to load from NBT (NBT format incorrect)
     */
    public AttackModeInstance(NbtCompound nbt) throws RuntimeException {
        this.attackMode = ElementRegistry.getAttackMode(nbt.getString("mode"));
        if (this.attackMode == null) throw new RuntimeException("Failed to load from NBT.");
        this.data = nbt.getCompound("data");
        if (!this.attackMode.verify(this.data)) throw new RuntimeException("Failed to load from NBT.");
    }

    /**
     * Save to an NBT.
     */
    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("mode", this.attackMode.getId());
        nbt.put("data", this.data);
        return nbt;
    }

    public AttackMode getAttackMode() {
        return attackMode;
    }

    public NbtCompound getData() {
        return data;
    }
}
