package dev.doublekekse.area_tools.mixin.attribute;

import dev.doublekekse.area_tools.attribute.AreaLayers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {
    @Inject(method = "addDynamicLayers", at = @At("RETURN"))
    private static void addDynamicLayers(EnvironmentAttributeSystem.Builder builder, Level level, CallbackInfo ci) {
        var areaLayers = new AreaLayers(level);
        var reg = BuiltInRegistries.ENVIRONMENT_ATTRIBUTE;

        var it = reg.iterator();
        int i = 0;

        while (it.hasNext()) {
            var attribute = it.next();

            if (attribute.isPositional()) {
                areaLayers.addLayer(builder, attribute, i);
            }

            i++;
        }
    }
}
