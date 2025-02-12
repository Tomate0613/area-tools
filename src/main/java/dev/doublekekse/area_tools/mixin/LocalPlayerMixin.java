package dev.doublekekse.area_tools.mixin;

import com.mojang.authlib.GameProfile;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.compat.FiguraCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Unique
    boolean wasInPanicArea = false;
    @Unique
    boolean previousPanicValue = false;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("figura")) {
            return;
        }

        var savedData = AreaLib.getSavedData(level());
        var area = savedData.get(AreaTools.id("figura_panic"));

        if (area == null) {
            return;
        }

        var inArea = area.contains(this);

        if (inArea && !wasInPanicArea) {
            previousPanicValue = FiguraCompat.isPanic();
            FiguraCompat.setPanic(false);
            wasInPanicArea = true;
        }
        if (!inArea && wasInPanicArea) {
            FiguraCompat.setPanic(previousPanicValue);
            wasInPanicArea = false;
        }
    }
}
