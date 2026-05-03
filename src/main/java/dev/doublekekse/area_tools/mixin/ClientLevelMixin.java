package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.duck.LocalPlayerDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    void disconnect(CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ((LocalPlayerDuck) player).area_tools$resetFiguraPanic();
    }
}
