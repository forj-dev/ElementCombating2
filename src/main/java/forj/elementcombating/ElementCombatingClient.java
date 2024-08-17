package forj.elementcombating;

import forj.elementcombating.element.network.PlayerBurstNetworking;
import forj.elementcombating.element.network.S2CPacketReceiver;
import forj.elementcombating.impl.entity.Entities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class ElementCombatingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        S2CPacketReceiver.init();
        PlayerBurstNetworking.initClient();
        registerKeyBind();
        Entities.initClient();
    }

    private void registerKeyBind() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (ElementCombating.SyncKey.wasPressed()) {
                ClientPlayNetworking.send(ElementCombating.SyncRequest, PacketByteBufs.create());
            }
        });
    }
}
