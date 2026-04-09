package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.duck.LocalPlayerDuck;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    /**
     * @reason We modify the return result of getShowDeathScreen(), however we want to copy the real (gamerule) value here.
     * If some other mod does the same it's a compat issue anyway, so I'd rather not silently fail
     */
    @Redirect(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;shouldShowDeathScreen()Z"))
    boolean replaceShowDeathScreen(LocalPlayer instance) {
        return ((LocalPlayerDuck) instance).area_tools$getShowDeathScreenReal();
    }
}
