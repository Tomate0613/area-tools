package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_lib.ExperimentalAreaUtils;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.component.area.RespawnPointComponent;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
    TeleportTransition respawn(ServerPlayer player, boolean consumeSpawnBlock, TeleportTransition.PostTeleportTransition postTeleportTransition, Operation<TeleportTransition> original) {
        var trackedAreas = AreaLib.getSavedData(player.level()).getEntityTrackedAreas(player);

        for (var area : trackedAreas) {
            var events = area.get(AreaComponents.EVENTS);
            if (events != null) {
                AreaTools.runCommands(server, player, events.onDeath);
            }
        }

        var smallestRespawn = ExperimentalAreaUtils.componentFor(AreaComponents.RESPAWN_POINT, trackedAreas);

        if (smallestRespawn == null) {
            return original.call(player, consumeSpawnBlock, postTeleportTransition);
        }

        return new TeleportTransition(player.level(), smallestRespawn.respawnPoint, Vec3.ZERO, smallestRespawn.respawnYaw, 0.0F, postTeleportTransition);
    }
}
