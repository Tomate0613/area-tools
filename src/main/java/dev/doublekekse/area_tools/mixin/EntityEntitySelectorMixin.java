package dev.doublekekse.area_tools.mixin;

import com.google.common.base.Predicates;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public class EntityEntitySelectorMixin {
    @WrapMethod(method = "pushableBy")
    private static Predicate<Entity> pushableBy(Entity entity, Operation<Predicate<Entity>> original) {
        var data = AreaLib.getSavedData(entity.level());
        var noPushing = data.isInEntityTrackedAreaWith(AreaComponents.NO_PUSHING, entity);

        if (noPushing) {
            return Predicates.alwaysFalse();
        }

        return original.call(entity);
    }
}
