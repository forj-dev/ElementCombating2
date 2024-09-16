package forj.elementcombating.utils.attribute_creator;

import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.HashMap;

public class NbtMap extends HashMap<String, AttributeCreator.Num> {
    private final NbtCompound nbt;

    public NbtMap(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public AttributeCreator.Num get(Object key) {
        NbtElement element = nbt.get((String) key);
        if (!(element instanceof AbstractNbtNumber number)) return new AttributeCreator.Num(0);
        Number value = number.numberValue();
        if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte)
            return new AttributeCreator.Num(value.longValue());
        if (value instanceof Double || value instanceof Float)
            return new AttributeCreator.Num(value.doubleValue());
        return null;
    }
}
