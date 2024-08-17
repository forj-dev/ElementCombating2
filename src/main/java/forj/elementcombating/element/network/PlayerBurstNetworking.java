package forj.elementcombating.element.network;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementActions;
import forj.elementcombating.element.StatAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class PlayerBurstNetworking {
    public static void initClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (ElementCombating.BurstKey.wasPressed()) {
                ClientPlayNetworking.send(ElementCombating.PlayerBurst, PacketByteBufs.create());
            }
        });
    }

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(ElementCombating.PlayerBurst,
                (server, player, handler, buf, responseSender) -> server.execute(
                        () -> ElementActions.entityBurst(player)));
        ServerPlayNetworking.registerGlobalReceiver(ElementCombating.SyncRequest,
                (server, player, handler, buf, responseSender) -> server.execute(() -> {
                    NbtCompound nbt = ((StatAccessor) player).getBurstAttribute().save();
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeNbt(nbt);
                    ServerPlayNetworking.send(player, ElementCombating.AttributeSync, buffer);
                    ((StatAccessor) player).getChargeManager().sync();
                }));
    }
}
