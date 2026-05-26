package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doublekekse.area_lib.AreaLib;
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

        var smallest = Double.MAX_VALUE;
        Difficulty difficulty = original.call(instance);

        for (var area : areas) {
            var bb = area.getBoundingBox();

            if (bb == null) {
                continue;
            }

            var size = bb.getSize();

            if (size >= smallest) {
                continue;
            }

            var diff = area.get(AreaComponents.FOOD_DIFFICULTY);

            if (diff == null) {
                continue;
            }

            difficulty = diff;
            smallest = size;
        }

        return difficulty;
    }

//    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
//    <T> T getGameRule(GameRules instance, GameRule<T> gameRule, Operation<T> original) {
//        if(gameRule != GameRules.NATURAL_HEALTH_REGENERATION) {
//            return original.call(instance, gameRule);
//        }
//        return null;
//    }
}
