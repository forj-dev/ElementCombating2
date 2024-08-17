package forj.elementcombating.impl;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementRegistry;
import forj.elementcombating.element.ElementType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class Utils {
    public static final TrackedDataHandler<Optional<ElementType>> OptionalElementTypeTrackedDataHandler = new TrackedDataHandler<>() {
        @Override
        public void write(PacketByteBuf buf, Optional<ElementType> value) {
            buf.writeBoolean(value.isPresent());
            value.ifPresent(type -> buf.writeString(type.getId()));
        }

        @Override
        public Optional<ElementType> read(PacketByteBuf buf) {
            if (!buf.readBoolean()) {
                return Optional.empty();
            }
            String id = buf.readString();
            return Optional.of(ElementRegistry.getElementTypes().get(id));
        }

        @Override
        public Optional<ElementType> copy(Optional<ElementType> value) {
            return value;
        }
    };

    public static Vec3d randomVec3d(double scale) {
        return new Vec3d(ElementCombating.RANDOM.nextDouble() - 0.5, ElementCombating.RANDOM.nextDouble() - 0.5, ElementCombating.RANDOM.nextDouble() - 0.5).normalize().multiply(scale);
    }

    public static void init() {
        TrackedDataHandlerRegistry.register(OptionalElementTypeTrackedDataHandler);
    }
}
