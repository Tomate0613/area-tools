package dev.doublekekse.area_tools.mixin;

import com.mojang.authlib.GameProfile;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_lib.data.AreaSavedData;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.duck.ServerPlayerDuck;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ServerPlayerDuck {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    protected abstract boolean isPvpAllowed();

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Unique
    Collection<Area> oldTrackedAreas;
    @Unique
    AreaSavedData data;


    @Inject(method = "<init>", at = @At("RETURN"))
    void init(MinecraftServer server, ServerLevel level, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        data = AreaLib.getSavedData(server);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        if(oldTrackedAreas == null) {
            oldTrackedAreas = data.getEntityTrackedAreas(this);
        }

        var trackItems = data.getEntityTrackedAreas(this);

        var newItems = trackItems.stream().filter(a -> !oldTrackedAreas.contains(a));
        var oldItems = oldTrackedAreas.stream().filter(a -> !trackItems.contains(a));

        newItems.forEach(area -> {
            var component = area.get(AreaComponents.EVENTS);

            if (component != null) {
                AreaTools.runCommands(server, this, component.onEnter);
            }
        });

        oldItems.forEach(area -> {
            var component = area.get(AreaComponents.EVENTS);

            if (component != null) {
                AreaTools.runCommands(server, this, component.onExit);
            }
        });

        oldTrackedAreas = trackItems;
    }

    @Override
    public Collection<Area> area_tools$getAreas() {
        return oldTrackedAreas;
    }

    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    void canHarmPlayer(Player target, CallbackInfoReturnable<Boolean> cir) {
        var savedData = AreaLib.getSavedData(target.level());
        var isToggled = savedData.isInEntityTrackedAreaWith(AreaComponents.PVP_TOGGLED, this);

        if (isToggled) {
            cir.setReturnValue(!isPvpAllowed());
        }
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    void restoreFrom(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        oldTrackedAreas = ((ServerPlayerDuck) oldPlayer).area_tools$getAreas();
    }
}
