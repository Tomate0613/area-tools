package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.duck.ServerPlayerDuck;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private PlayerDataStorage playerIo;

    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/DimensionTransition$PostDimensionTransition;)Lnet/minecraft/world/level/portal/DimensionTransition;"))
    DimensionTransition respawn(ServerPlayer instance, boolean bl, DimensionTransition.PostDimensionTransition postDimensionTransition, Operation<DimensionTransition> original) {
        var areas = ((ServerPlayerDuck) instance).area_tools$getAreas();
        var item = areas.stream().min(Comparator.comparingDouble(area -> area.getBoundingBox(AreaLib.getSavedData(instance.level())).getSize()));

        if (item.isEmpty() || item.get().respawnPoint == null) {
            return original.call(instance, bl, postDimensionTransition);
        }

        return new DimensionTransition((ServerLevel) instance.level(), item.get().respawnPoint, Vec3.ZERO, item.get().respawnYaw, 0.0f, postDimensionTransition);
    }
}
