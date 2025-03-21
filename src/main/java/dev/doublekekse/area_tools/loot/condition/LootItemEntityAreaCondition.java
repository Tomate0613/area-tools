package dev.doublekekse.area_tools.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.registry.AreaLootConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record LootItemEntityAreaCondition(
    ResourceLocation areaId,
    LootContext.EntityTarget entityTarget
) implements LootItemCondition {
    public static MapCodec<LootItemEntityAreaCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("area_id").forGetter(LootItemEntityAreaCondition::areaId),
        LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(LootItemEntityAreaCondition::entityTarget)
    ).apply(instance, LootItemEntityAreaCondition::new));


    @Override
    public @NotNull LootItemConditionType getType() {
        return AreaLootConditions.AREA_CHECK;
    }

    @Override
    public boolean test(LootContext lootContext) {
        var area = AreaLib.getServerArea(lootContext.getLevel().getServer(), areaId);
        return area != null && area.contains(lootContext.getParam(this.entityTarget.getParam()));
    }
}
