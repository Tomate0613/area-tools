package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.duck.EntitySelectorDuck;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public class EntitySelectorMixin implements EntitySelectorDuck {
    @Shadow
    @Final
    private MinMaxBounds.Doubles range;
    @Unique
    private ResourceLocation areaId;

    @Override
    public void area_tools$setArea(ResourceLocation area) {
        this.areaId = area;
    }

    @ModifyVariable(method = "getPredicate", at = @At(value = "STORE"), ordinal = 2)
    boolean enterIfBranch(boolean value) {
        if (areaId != null) {
            return true;
        }

        return value;
    }

    //List<Predicate<Entity>>
    @WrapOperation(method = "getPredicate", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    boolean getPredicate(List<Predicate<Entity>> predicates, Object o, Operation<Boolean> original) {
        if (areaId != null) {
            predicates.add((entity) -> {
                var area = AreaLib.getSavedData(entity.level()).get(areaId);
                if (area == null) {
                    return false;
                }

                return area.contains(entity);
            });
        }

        if (range != null) {
            return original.call(predicates, o);
        }

        return true;
    }
}
