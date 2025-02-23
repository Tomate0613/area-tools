package dev.doublekekse.area_tools.data;

import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.bvh.BVHItem;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrackedAreaItem implements BVHItem {
    public Area area;

    public List<String> onEnter = new ArrayList<>();
    public List<String> onExit = new ArrayList<>();
    public @Nullable Vec3 respawnPoint;
    public float respawnYaw;

    public static TrackedAreaItem of(AreaSavedData savedData, CompoundTag tag) {
        var data = new TrackedAreaItem();
        data.load(savedData, tag);

        return data;
    }

    public void update(AreaSavedData savedData) {
        area = savedData.get(area.getId());
    }

    public CompoundTag save() {
        var tag = new CompoundTag();

        tag.putString("areaId", area.getId().toString());
        tag.put("onEnter", toTag(onEnter));
        tag.put("onExit", toTag(onExit));

        if (respawnPoint != null) {
            tag.put("respawnPoint", toTag(respawnPoint));
            tag.putFloat("respawnYaw", respawnYaw);
        }

        return tag;
    }

    public void load(AreaSavedData savedData, CompoundTag tag) {
        onEnter = toStringList(tag.getList("onEnter", Tag.TAG_STRING));
        onExit = toStringList(tag.getList("onExit", Tag.TAG_STRING));
        var areaId = ResourceLocation.parse(tag.getString("areaId"));
        area = savedData.get(areaId);

        if (tag.contains("respawnPoint")) {
            respawnPoint = toVec3(tag.getCompound("respawnPoint"));
            respawnYaw = tag.getFloat("respawnYaw");
        }
    }

    public ListTag toTag(Collection<?> list) {
        var listTag = new ListTag();

        for (var value : list) {
            listTag.add(StringTag.valueOf(String.valueOf(value)));
        }

        return listTag;
    }

    public CompoundTag toTag(Vec3 pos) {
        var tag = new CompoundTag();

        tag.putDouble("x", pos.x);
        tag.putDouble("y", pos.y);
        tag.putDouble("z", pos.z);

        return tag;
    }

    public Vec3 toVec3(CompoundTag tag) {
        return new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }

    public List<String> toStringList(ListTag listTag) {
        var list = new ArrayList<String>();

        for (var value : listTag) {
            list.add(value.getAsString());
        }

        return list;
    }

    public boolean isEmpty() {
        return onEnter.isEmpty() && onExit.isEmpty() && respawnPoint == null;
    }


    @Override
    public String toString() {
        return "TrackItem{" +
            ", area=" + area +
            ", onEnter=" + onEnter +
            ", onExit=" + onExit +
            '}';
    }

    @Override
    public boolean contains(Level level, Vec3 vec3) {
        return area.contains(level, vec3);
    }

    @Override
    public @Nullable AABB getBoundingBox() {
        return area.getBoundingBox();
    }
}
