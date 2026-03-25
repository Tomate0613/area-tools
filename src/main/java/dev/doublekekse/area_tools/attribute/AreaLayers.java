package dev.doublekekse.area_tools.attribute;

import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;

// TODO caching
public class AreaLayers {
    Level level;

    public AreaLayers(Level level) {
        this.level = level;
    }

    @SuppressWarnings("unchecked")
    private <T> EnvironmentAttributeLayer.Positional<T> layer(EnvironmentAttribute<T> attribute) {
        return (baseValue, pos, _) -> {
            var savedData = AreaLib.getSavedData(level);
            var areas = savedData.findTrackedAreasContaining(level, pos);

            var optionalArea = areas
                .stream()
                .filter(a -> a.has(AreaComponents.ENVIRONMENT_ATTRIBUTES_COMPONENT))
                .min(AreaTools.smallestArea());

            return (T) optionalArea.map(area -> {
                var ea = area.get(AreaComponents.ENVIRONMENT_ATTRIBUTES_COMPONENT);

                var sampler = ea.getSampler((EnvironmentAttribute<Object>) attribute, level.clockManager(), level);
                if (sampler != null) {
                    assert ea.timeline != null;
                    return sampler.applyTimeBased(baseValue, (int) ea.timeline.getCurrentTicks(level.clockManager()));
                }

                return ea.attributes.get(attribute);
            }).orElse(baseValue);
        };
    }

    public <T> void addLayer(EnvironmentAttributeSystem.Builder builder, EnvironmentAttribute<T> attribute) {
        builder.addPositionalLayer(attribute, layer(attribute));
    }
}
