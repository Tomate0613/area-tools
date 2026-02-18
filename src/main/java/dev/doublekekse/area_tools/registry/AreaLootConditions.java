package dev.doublekekse.area_tools.registry;

import com.mojang.serialization.MapCodec;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.loot.condition.LootItemEntityAreaCondition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AreaLootConditions {
    public static MapCodec<? extends LootItemCondition> AREA_CHECK = register(LootItemEntityAreaCondition.CODEC, "area_check");

    private static MapCodec<? extends LootItemCondition> register(MapCodec<? extends LootItemCondition> codec, String path) {
        return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, AreaTools.id(path), codec);
    }

    public static void register() {

    }
}
