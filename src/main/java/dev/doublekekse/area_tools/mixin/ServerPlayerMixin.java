package dev.doublekekse.area_tools.mixin;

import com.mojang.authlib.GameProfile;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.data.AreaToolsSavedData;
import dev.doublekekse.area_tools.data.TrackedAreaItem;
import dev.doublekekse.area_tools.duck.ServerPlayerDuck;
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
    List<TrackedAreaItem> oldTrackedAreaItems;
    @Unique
    AreaToolsSavedData data;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    void init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        data = AreaToolsSavedData.getServerData(minecraftServer);
        oldTrackedAreaItems = data.trackedAreas.findAreasContaining(level(), position());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        var trackItems = data.trackedAreas.findAreasContaining(level(), position());

        var newItems = trackItems.stream().filter(a -> !oldTrackedAreaItems.contains(a));
        var oldItems = oldTrackedAreaItems.stream().filter(a -> !trackItems.contains(a));

        newItems.forEach(area -> {
            AreaTools.runCommands(server, this, area.onEnter);
        });

        oldItems.forEach(area -> {
            AreaTools.runCommands(server, this, area.onExit);
        });

        oldTrackedAreaItems = trackItems;
    }

    @Override
    public List<TrackedAreaItem> area_tools$getAreas() {
        return oldTrackedAreaItems;
    }
}
