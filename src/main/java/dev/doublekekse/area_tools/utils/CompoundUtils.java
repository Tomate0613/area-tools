package dev.doublekekse.area_tools.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;
import java.util.function.Function;

public class CompoundUtils {
    public static <Key, Value> HashMap<Key, Value> toMap(CompoundTag tag, Function<String, Key> toKey, Function<Tag, Value> toValue) {
        var map = new HashMap<Key, Value>();

        for (var key : tag.getAllKeys()) {
            map.put(toKey.apply(key), toValue.apply(tag.get(key)));
        }

        return map;
    }

    public static <Key, Value> CompoundTag toTag(Map<Key, Value> map, Function<Key, String> toKey, Function<Value, Tag> toValue) {
        var compoundTag = new CompoundTag();

        for (var entry : map.entrySet()) {
            compoundTag.put(toKey.apply(entry.getKey()), toValue.apply(entry.getValue()));
        }

        return compoundTag;
    }

    public static <Value> HashSet<Value> toSet(ListTag listTag, Function<Tag, Value> toValue) {
        var set = new HashSet<Value>();
        for (var tag : listTag) {
            set.add(toValue.apply(tag));
        }

        return set;
    }

    public static <Value> ListTag toTag(Collection<Value> collection, Function<Value, Tag> toValue) {
        var listTag = new ListTag();

        for (var value : collection) {
            listTag.add(toValue.apply(value));
        }

        return listTag;
    }
}
