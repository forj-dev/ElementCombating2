package forj.elementcombating.element.network;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ChargeManager;
import forj.elementcombating.element.ElementType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPlayerChargeManager extends ChargeManager {

    private final ServerPlayerEntity player;

    public ServerPlayerChargeManager(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    protected void onUpdate(ElementType elementType, float charge) {
        try {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(elementType.getId());
            buf.writeFloat(charge);
            ServerPlayNetworking.send(this.player, ElementCombating.ChargeSync, buf);
            super.onUpdate(elementType, charge);
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    protected void onClear() {
        try {
            ServerPlayNetworking.send(this.player, ElementCombating.ChargeClear, PacketByteBufs.empty());
            super.onClear();
        } catch (NullPointerException ignored) {
        }
    }
}
