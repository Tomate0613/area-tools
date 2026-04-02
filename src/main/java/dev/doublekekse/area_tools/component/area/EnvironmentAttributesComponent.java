package dev.doublekekse.area_tools.component.area;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.AttributeTrackSampler;
import net.minecraft.world.timeline.Timeline;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentAttributesComponent implements AreaDataComponent {
    public Map<EnvironmentAttribute<Object>, Object> attributes = new HashMap<>();
    @Nullable
    public Timeline timeline;
    @Nullable
    public Identifier timelineIdentifier;

    public Map<EnvironmentAttribute<Object>, AttributeTrackSampler<Object, ?>> samplers = new HashMap<>();

    public @Nullable AttributeTrackSampler<Object, ?> getSampler(EnvironmentAttribute<Object> attribute, ClockManager clockManager, Level level) {
        if (timelineIdentifier == null) {
            return null;
        }

        if (!samplers.containsKey(attribute)) {
            var timeline = getTimeline(level);

            if (timeline == null) {
                return null;
            }

            if (!timeline.attributes().contains(attribute)) {
                return null;
            }
            samplers.put(attribute, timeline.createTrackSampler(attribute, clockManager));
        }

        return samplers.get(attribute);
    }

    public @Nullable Timeline getTimeline(Level level) {
        if (timelineIdentifier == null) {
            return null;
        }

        if (timeline != null) {
            return timeline;
        }


        var timelines = level.holderLookup(Registries.TIMELINE);
        var key = ResourceKey.create(Registries.TIMELINE, timelineIdentifier);
        var t = timelines.get(key);

        if (t.isEmpty()) {
            return null;
        }

        timeline = t.get().value();
        return timeline;
    }

    public boolean overridesAttribute(EnvironmentAttribute<?> attribute, Level level) {
        if (attributes.get(attribute) != null) {
            return true;
        }

        if (samplers.get(attribute) != null) {
            return true;
        }

        var timeline = getTimeline(level);
        if (timeline != null) {
            return timeline.attributes().contains(attribute);
        }

        return false;
    }

    public void setTimeline(Identifier id) {
        timeline = null;
        timelineIdentifier = id;
    }

    public void setTimeline(Holder.Reference<Timeline> timelineReference) {
        timeline = timelineReference.value();
        timelineIdentifier = timelineReference.key().identifier();
    }

    public void resetTimeline() {
        timeline = null;
        timelineIdentifier = null;
    }

    public boolean isEmpty() {
        return timelineIdentifier == null && attributes.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(AreaSavedData savedData, CompoundTag compoundTag) {
        attributes = new HashMap<>();

        var attributesTag = compoundTag.getCompoundOrEmpty("attributes");

        for (var entry : attributesTag.entrySet()) {
            var id = Identifier.parse(entry.getKey());
            BuiltInRegistries.ENVIRONMENT_ATTRIBUTE.getOptional(id).ifPresent(environmentAttribute -> {
                environmentAttribute.valueCodec().parse(NbtOps.INSTANCE, entry.getValue()).ifSuccess(value -> {
                    attributes.put((EnvironmentAttribute<Object>) environmentAttribute, value);
                });
            });
        }

        compoundTag.getString("timeline").ifPresent(s -> setTimeline(Identifier.parse(s)));
    }

    @Override
    public CompoundTag save() {
        var tag = new CompoundTag();

        var attributesTag = new CompoundTag();

        for (var entry : attributes.entrySet()) {
            entry.getKey().valueCodec().encodeStart(NbtOps.INSTANCE, entry.getValue()).ifSuccess(v -> {
                var id = BuiltInRegistries.ENVIRONMENT_ATTRIBUTE.getKey(entry.getKey());

                if (id == null) {
                    return;
                }

                attributesTag.put(id.toString(), v);
            });
        }

        tag.put("attributes", attributesTag);

        if (timelineIdentifier != null) {
            tag.putString("timeline", timelineIdentifier.toString());
        }

        return tag;
    }
}
