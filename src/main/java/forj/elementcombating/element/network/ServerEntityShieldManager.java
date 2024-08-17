package forj.elementcombating.element.network;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.ShieldManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerEntityShieldManager extends ShieldManager {

    private final LivingEntity entity;

    public ServerEntityShieldManager(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    protected void onUpdate(boolean fromSkill, ElementType elementType, int duration, int health) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(this.entity.getId());
        buf.writeBoolean(fromSkill);
        buf.writeString(elementType.getId());
        buf.writeVarInt(duration);
        buf.writeVarInt(health);
        for (ServerPlayerEntity player : PlayerLookup.tracking(this.entity))
            ServerPlayNetworking.send(player, ElementCombating.ShieldSync, buf);
        if (this.entity instanceof ServerPlayerEntity player)
            ServerPlayNetworking.send(player, ElementCombating.ShieldSync, buf);
        super.onUpdate(fromSkill, elementType, duration, health);
    }
}
