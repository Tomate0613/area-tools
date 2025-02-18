package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.duck.LocalPlayerDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    void disconnect(Screen screen, boolean bl, CallbackInfo ci) {
        if (player == null) {
            return;
        }

        ((LocalPlayerDuck) player).area_tools$resetFiguraPanic();
    }
}
