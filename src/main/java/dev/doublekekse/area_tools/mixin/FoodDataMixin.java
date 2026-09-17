package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_lib.ExperimentalAreaUtils;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    Difficulty getDifficulty(ServerLevel instance, Operation<Difficulty> original, @Local(argsOnly = true, name = "player") ServerPlayer player) {
        var areas = AreaLib.getSavedData(player.level()).getEntityTrackedAreas(player);
        var diff = ExperimentalAreaUtils.componentFor(AreaComponents.FOOD_DIFFICULTY, areas);

        return diff != null
            ? diff
            : original.call(instance);
    }
}
