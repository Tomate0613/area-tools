package dev.doublekekse.area_tools.mixin;

import com.mojang.authlib.GameProfile;
import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.compat.FiguraCompat;
import dev.doublekekse.area_tools.duck.LocalPlayerDuck;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer implements LocalPlayerDuck {
    @Shadow
    private boolean showDeathScreen;
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

        var savedData = AreaClientData.getClientLevelData();

        if (savedData == null) {
            return;
        }

        var shouldPanic = savedData.isInEntityTrackedAreaWith(AreaComponents.FIGURA_PANIC, this);

        if (shouldPanic && !wasInPanicArea) {
            previousPanicValue = FiguraCompat.isPanic();
            FiguraCompat.setPanic(true);
            wasInPanicArea = true;
        }
        if (!shouldPanic && wasInPanicArea) {
            FiguraCompat.setPanic(previousPanicValue);
            wasInPanicArea = false;
        }
    }

    @Override
    public void area_tools$resetFiguraPanic() {
        if (!FabricLoader.getInstance().isModLoaded("figura")) {
            return;
        }

        if (wasInPanicArea) {
            FiguraCompat.setPanic(previousPanicValue);
            wasInPanicArea = false;
        }
    }

    @Override
    public boolean area_tools$getShowDeathScreenReal() {
        return showDeathScreen;
    }

    @Inject(method = "shouldShowDeathScreen", at = @At("HEAD"), cancellable = true)
    void shouldShowDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        var data = AreaClientData.getClientLevelData();
        var areas = data.getEntityTrackedAreas(this);
        var area = areas.stream().filter(a -> a.has(AreaComponents.RESPAWN_POINT_COMPONENT)).min(AreaTools.smallestArea());

        area.ifPresent(value -> cir.setReturnValue(!value.get(AreaComponents.RESPAWN_POINT_COMPONENT).skipDeathScreen));
    }
}
