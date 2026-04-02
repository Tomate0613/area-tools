package dev.doublekekse.area_tools.attribute;

import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AreaLayers {
    Level level;

    private Vec3 lastSampledPosition;
    private List<Area> areas;

    private static boolean overridesDirty = true;
    private static boolean[] anyOverrides;

    public AreaLayers(Level level) {
        this.level = level;
    }

    @SuppressWarnings("unchecked")
    private <T> EnvironmentAttributeLayer.Positional<T> layer(EnvironmentAttribute<T> attribute, int index) {
        return (baseValue, pos, _) -> {
            if (overridesDirty) {
                checkOverrides();
                overridesDirty = false;
            }

            if (!anyOverrides[index]) {
                return baseValue;
            }

            if (!pos.equals(this.lastSampledPosition)) {
                this.lastSampledPosition = pos;

                var savedData = AreaLib.getSavedData(level);
                this.areas = savedData.findTrackedAreasContaining(level, pos, area -> area.has(AreaComponents.ENVIRONMENT_ATTRIBUTES_COMPONENT));
            }

            T value = null;
            var minSize = Double.MAX_VALUE;

            for (var area : areas) {
                var bb = area.getBoundingBox();
                if (bb == null) {
                    continue;
                }

                var areaSize = area.getBoundingBox().getSize();
                if (value != null && minSize < areaSize) {
                    continue;
                }

                var ea = area.get(AreaComponents.ENVIRONMENT_ATTRIBUTES_COMPONENT);

                var sampler = ea.getSampler((EnvironmentAttribute<Object>) attribute, level.clockManager(), level);
                if (sampler != null) {
                    assert ea.timeline != null;
                    value = (T) sampler.applyTimeBased(baseValue, (int) ea.timeline.getCurrentTicks(level.clockManager()));
                    minSize = areaSize;
                    continue;
                }

                var v = (T) ea.attributes.get(attribute);

                if (v != null) {
                    minSize = areaSize;
                    value = v;
                }
            }

            if (value == null) {
                return baseValue;
            }

            return value;
        };
    }

    public <T> void addLayer(EnvironmentAttributeSystem.Builder builder, EnvironmentAttribute<T> attribute, int index) {
        builder.addPositionalLayer(attribute, layer(attribute, index));
    }

    private void checkOverrides() {
        var savedData = AreaLib.getSavedData(level);
        var reg = BuiltInRegistries.ENVIRONMENT_ATTRIBUTE;

        var it = reg.iterator();
        int i = 0;

        var anyOverrides = new boolean[reg.size()];

        while (it.hasNext()) {
            var attribute = it.next();
            var overriden = false;

            for (var area : savedData.getAreas()) {
                var ea = area.get(AreaComponents.ENVIRONMENT_ATTRIBUTES_COMPONENT);
                if (ea != null && ea.overridesAttribute(attribute, level)) {
                    overriden = true;
                    break;
                }
            }

            anyOverrides[i] = overriden;
            i++;
        }

        AreaLayers.anyOverrides = anyOverrides;
    }

    public static void registerListeners() {
        AreaLib.addListener(_ -> {
            overridesDirty = true;
        });
    }
}
