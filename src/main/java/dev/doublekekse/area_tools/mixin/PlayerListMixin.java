package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.duck.ServerPlayerDuck;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
    TeleportTransition respawn(ServerPlayer instance, boolean bl, TeleportTransition.PostTeleportTransition postTeleportTransition, Operation<TeleportTransition> original) {
        //DimensionTransition respawn(ServerPlayer instance, boolean bl, DimensionTransition.PostDimensionTransition postDimensionTransition, Operation<DimensionTransition> original) {
        //DimensionTransition respawn(ServerPlayer instance, boolean bl, DimensionTransition.PostDimensionTransition postDimensionTransition, Operation<DimensionTransition> original) {
        var areas = ((ServerPlayerDuck) instance).area_tools$getAreas();
        var area = areas.stream().filter(a -> a.has(AreaComponents.RESPAWN_POINT_COMPONENT)).min(AreaTools.smallestArea());

        if (area.isEmpty()) {
            return original.call(instance, bl, postTeleportTransition);
        }

        var component = area.get().get(AreaComponents.RESPAWN_POINT_COMPONENT);
        return new TeleportTransition(instance.level(), component.respawnPoint, Vec3.ZERO, component.respawnYaw, 0.0F, postTeleportTransition);
    }
}
