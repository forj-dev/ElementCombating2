package forj.elementcombating.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends EntityMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(MinecraftServer server, ServerWorld world, GameProfile profile, CallbackInfo ci) {
        this.setServerPlayer((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.copyCustomManagerFrom(oldPlayer);
    }
}
