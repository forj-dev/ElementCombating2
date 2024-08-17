package forj.elementcombating.element.network;

import forj.elementcombating.ElementCombating;
import forj.elementcombating.element.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;

public class S2CPacketReceiver {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ElementCombating.CoolDownSync,
                (client, handler, buf, responseSender) -> {
                    AttributeType attributeType = AttributeType.typeList[buf.readByte()];
                    ElementType elementType = ElementRegistry.getElementTypes().get(buf.readString());
                    int duration = buf.readVarInt();
                    client.execute(() -> {
                        if (client.player == null) return;
                        CoolDownManager coolDownManager = ((StatAccessor) client.player).getCoolDownManager();
                        if (duration > 0)
                            coolDownManager.set(attributeType, elementType, duration);
                        else
                            coolDownManager.remove(attributeType, elementType);
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(ElementCombating.ChargeSync,
                (client, handler, buf, responseSender) -> {
                    ElementType elementType = ElementRegistry.getElementTypes().get(buf.readString());
                    float charge = buf.readFloat();
                    client.execute(() -> {
                        if (client.player == null) return;
                        ChargeManager chargeManager = ((StatAccessor) client.player).getChargeManager();
                        if (charge > 0)
                            chargeManager.set(elementType, charge);
                        else
                            chargeManager.remove(elementType);
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(ElementCombating.ChargeClear,
                (client, handler, buf, responseSender) -> client.execute(() -> {
                    if (client.player == null) return;
                    ((StatAccessor) client.player).getChargeManager().clear();
                }));
        ClientPlayNetworking.registerGlobalReceiver(ElementCombating.AttributeSync,
                (client, handler, buf, responseSender) -> {
                    NbtCompound burst = buf.readNbt();
                    client.execute(() -> {
                        if (client.player == null) return;
                        StatAccessor accessor = (StatAccessor) client.player;
                        if (burst != null) {
                            accessor.setBurstAttribute(new ElementAttribute(burst));
                        }
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(ElementCombating.ShieldSync,
                (client, handler, buf, responseSender) -> {
                    int entityId = buf.readVarInt();
                    boolean fromSkill = buf.readBoolean();
                    ElementType elementType = ElementRegistry.getElementTypes().get(buf.readString());
                    int duration = buf.readVarInt();
                    int health = buf.readVarInt();
                    client.execute(() -> {
                        if (client.player == null) return;
                        Entity entity = client.player.getWorld().getEntityById(entityId);
                        if (!(entity instanceof LivingEntity target)) return;
                        if (duration > 0)
                            ((StatAccessor) target).getShieldManager()
                                    .set(fromSkill, elementType, duration, health);
                        else
                            ((StatAccessor) target).getShieldManager()
                                    .remove(fromSkill, elementType);
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(ElementCombating.EffectSync,
                (client, handler, buf, responseSender) -> {
                    int entityId = buf.readVarInt();
                    ElementEffect effect = ElementRegistry.getElementEffect(buf.readString());
                    int duration = buf.readVarInt();
                    client.execute(() -> {
                        if (client.player == null) return;
                        Entity entity = client.player.getWorld().getEntityById(entityId);
                        if (!(entity instanceof LivingEntity target)) return;
                        if (duration > 0)
                            target.setStatusEffect(new StatusEffectInstance(
                                            effect, duration, 0, false, false)
                                    , null);
                        else
                            target.removeStatusEffect(effect);
                    });
                });
    }
}
