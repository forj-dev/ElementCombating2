package forj.elementcombating.element.network;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.AttributeType;
import forj.elementcombating.element.CoolDownManager;
import forj.elementcombating.element.ElementType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPlayerCoolDownManager extends CoolDownManager {

    private final ServerPlayerEntity player;

    public ServerPlayerCoolDownManager(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    protected void onUpdate(AttributeType attributeType, ElementType elementType, int duration) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(attributeType.getIndex());
        buf.writeString(elementType.getId());
        buf.writeVarInt(duration);
        ServerPlayNetworking.send(this.player, ElementCombating.CoolDownSync, buf);
        super.onUpdate(attributeType, elementType, duration);
    }
}
