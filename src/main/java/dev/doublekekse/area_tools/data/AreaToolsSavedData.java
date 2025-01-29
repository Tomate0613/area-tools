package dev.doublekekse.area_tools.data;

import dev.doublekekse.area_lib.data.AreaSavedData;
import dev.doublekekse.area_tools.utils.CompoundUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AreaToolsSavedData extends SavedData {
    public final TrackBVHTree trackedAreas;

    public AreaToolsSavedData(AreaSavedData areaSavedData) {
        trackedAreas = new TrackBVHTree(areaSavedData);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("tracked_areas", trackedAreas.save());
        //compoundTag.put("player_map", toTag(playerMap));

        return compoundTag;
    }

    public static AreaToolsSavedData load(AreaSavedData savedData, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var data = new AreaToolsSavedData(savedData);

        data.trackedAreas.load(compoundTag.getCompound("tracked_areas"));
        //data.playerMap = data.toPlayerMap(compoundTag);

        return data;
    }

    public CompoundTag toTag(Map<UUID, Set<ResourceLocation>> list) {
        return CompoundUtils.toTag(list, k -> String.valueOf(String.valueOf(k)), this::toTag);
    }

    public ListTag toTag(Set<ResourceLocation> set) {
        return CompoundUtils.toTag(set, v -> StringTag.valueOf(v.toString()));
    }

    public static HashSet<ResourceLocation> toResourceLocationSet(ListTag listTag) {
        return CompoundUtils.toSet(listTag, v -> ResourceLocation.parse(v.getAsString()));
    }

    public HashMap<UUID, Set<ResourceLocation>> toPlayerMap(CompoundTag tag) {
        return CompoundUtils.toMap(tag, UUID::fromString, v -> toResourceLocationSet((ListTag) v));
    }


    public static AreaToolsSavedData getServerData(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();

        final var areaSavedData = AreaSavedData.getServerData(server);

        final var factory = new SavedData.Factory<>(
            () -> new AreaToolsSavedData(areaSavedData),
            (compoundTag, provider) -> load(areaSavedData, compoundTag, provider),
            null
        );

        AreaToolsSavedData data = persistentStateManager.computeIfAbsent(factory, "area_tools");
        data.setDirty();

        return data;
    }

}
