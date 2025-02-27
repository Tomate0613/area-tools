package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
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

    @WrapOperation(method = "getPredicate", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/MinMaxBounds$Doubles;isAny()Z"))
    boolean isAny(MinMaxBounds.Doubles instance, Operation<Boolean> original) {
        if (areaId != null) {
            return false;
        }

        return original.call(instance);
    }

    @Inject(method = "getPredicate", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    void getPredicate(CallbackInfoReturnable<Predicate<Entity>> cir, @Local(ordinal = 1) List<Predicate<Entity>> predicates) {
        if (areaId == null) {
            return;
        }

        predicates.add((entity) -> {
            var area = AreaLib.getServerArea(Objects.requireNonNull(entity.getServer()), areaId);
            if (area == null) {
                return false;
            }

            return area.contains(entity);
        });
    }

    @WrapWithCondition(method = "getPredicate", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
    <E> boolean addAABBPredicate(List<E> instance, E e) {
        return !range.isAny();
    }
}
