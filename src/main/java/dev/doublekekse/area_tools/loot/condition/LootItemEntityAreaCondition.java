package dev.doublekekse.area_tools.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.area_lib.AreaLib;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record LootItemEntityAreaCondition(
    Identifier areaId,
    LootContext.EntityTarget entityTarget
) implements LootItemCondition {
    public static MapCodec<LootItemEntityAreaCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Identifier.CODEC.fieldOf("area_id").forGetter(LootItemEntityAreaCondition::areaId),
        LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(LootItemEntityAreaCondition::entityTarget)
    ).apply(instance, LootItemEntityAreaCondition::new));

    @Override
    public boolean test(LootContext lootContext) {
        var area = AreaLib.getServerArea(lootContext.getLevel().getServer(), areaId);
        return area != null && area.contains(lootContext.getParameter(this.entityTarget.contextParam()));
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }
}
