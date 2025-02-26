package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doublekekse.area_tools.duck.ServerPlayerDuck;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/DimensionTransition$PostDimensionTransition;)Lnet/minecraft/world/level/portal/DimensionTransition;"))
    DimensionTransition respawn(ServerPlayer instance, boolean bl, DimensionTransition.PostDimensionTransition postDimensionTransition, Operation<DimensionTransition> original) {
        var areas = ((ServerPlayerDuck) instance).area_tools$getAreas();
        var area = areas.stream().filter(a -> a.has(AreaComponents.RESPAWN_POINT_COMPONENT)).min(Comparator.comparingDouble(a -> a.getBoundingBox().getSize()));

        if (area.isEmpty()) {
            return original.call(instance, bl, postDimensionTransition);
        }

        var component = area.get().get(AreaComponents.RESPAWN_POINT_COMPONENT);
        return new DimensionTransition((ServerLevel) instance.level(), component.respawnPoint, Vec3.ZERO, component.respawnYaw, 0.0f, postDimensionTransition);
    }
}
