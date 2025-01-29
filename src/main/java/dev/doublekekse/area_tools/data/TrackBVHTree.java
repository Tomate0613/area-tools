package dev.doublekekse.area_tools.data;

import dev.doublekekse.area_lib.bvh.BVHNode;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class TrackBVHTree {
    private BVHNode<TrackedAreaItem> node;
    private final AreaSavedData savedData;

    public TrackBVHTree(AreaSavedData savedData) {
        this.savedData = savedData;
    }

    public void add(TrackedAreaItem trackedAreaItem) {
        if (node == null) {
            node = new BVHNode<>(savedData, Collections.singletonList(trackedAreaItem));
            return;
        }

        node = node.with(savedData, trackedAreaItem);
    }

    public void remove(TrackedAreaItem trackedAreaItem) {
        if (node == null) {
            return;
        }

        node = node.without(savedData, trackedAreaItem);
    }

    public Optional<TrackedAreaItem> get(ResourceLocation areaId) {
        return listAllItems().stream().filter((trackItem) -> trackItem.areaId.equals(areaId)).findAny();
    }

    public TrackedAreaItem getOrCreate(ResourceLocation areaId) {
        var existing = get(areaId);

        if (existing.isPresent()) {
            return existing.get();
        }

        var item = new TrackedAreaItem();
        item.areaId = areaId;
        item.area = savedData.get(areaId);

        add(item);
        return item;
    }

    public boolean contains(Level level, Vec3 position) {
        if (node == null) {
            return false;
        }

        return node.contains(level, position);
    }

    public List<TrackedAreaItem> findAreasContaining(Level level, Vec3 position) {
        if (node == null) {
            return Collections.emptyList();
        }

        return node.findAreasContaining(level, position);
    }

    public List<TrackedAreaItem> listAllItems() {
        if (node == null) {
            return Collections.emptyList();
        }

        return node.listAllAreas();
    }

    public CompoundTag save() {
        var tag = new CompoundTag();
        var listTag = new ListTag();
        var trackItems = listAllItems();

        for (var trackItem : trackItems) {
            listTag.add(trackItem.save());
        }

        tag.put("track_items", listTag);

        return tag;
    }

    public void load(CompoundTag tag) {
        var listTag = tag.getList("track_items", Tag.TAG_COMPOUND);
        var items = new ArrayList<TrackedAreaItem>();

        for (var itemTag : listTag) {
            items.add(TrackedAreaItem.of(savedData, (CompoundTag) itemTag));
        }

        node = new BVHNode<>(savedData, items);
    }
}
