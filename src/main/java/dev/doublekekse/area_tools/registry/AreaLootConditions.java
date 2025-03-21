package dev.doublekekse.area_tools.registry;

import com.mojang.serialization.MapCodec;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.loot.condition.LootItemEntityAreaCondition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class AreaLootConditions {
    public static LootItemConditionType AREA_CHECK = register(LootItemEntityAreaCondition.CODEC, "area_check");

    private static LootItemConditionType register(MapCodec<? extends LootItemCondition> codec, String path) {
        return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, AreaTools.id(path), new LootItemConditionType(codec));
    }

    public static void register() {

    }
}
