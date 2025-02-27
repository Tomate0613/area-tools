package dev.doublekekse.area_tools.mixin;

import com.mojang.authlib.GameProfile;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.data.AreaSavedData;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.duck.ServerPlayerDuck;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.core.BlockPos;
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

import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ServerPlayerDuck {
    @Shadow
    @Final
    public MinecraftServer server;
    @Unique
    List<Area> oldTrackedAreas;
    @Unique
    AreaSavedData data;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    void init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        data = AreaSavedData.getServerData(minecraftServer);
        oldTrackedAreas = data.findTrackedAreasContaining(this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        var trackItems = data.findTrackedAreasContaining(this);

        var newItems = trackItems.stream().filter(a -> !oldTrackedAreas.contains(a));
        var oldItems = oldTrackedAreas.stream().filter(a -> !trackItems.contains(a));

        newItems.forEach(area -> {
            var component = area.get(AreaComponents.EVENTS_COMPONENT);

            if(component != null) {
                AreaTools.runCommands(server, this, component.onEnter);
            }
        });

        oldItems.forEach(area -> {
            var component = area.get(AreaComponents.EVENTS_COMPONENT);

            if(component != null) {
                AreaTools.runCommands(server, this, component.onExit);
            }
        });

        oldTrackedAreas = trackItems;
    }

    @Override
    public List<Area> area_tools$getAreas() {
        return oldTrackedAreas;
    }
}
