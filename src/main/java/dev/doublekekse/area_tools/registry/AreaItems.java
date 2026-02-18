package dev.doublekekse.area_tools.registry;

import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.item.AreaCreatorItem;
import dev.doublekekse.area_tools.item.SpawnpointSetterItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class AreaItems {
    public static final Item AREA_CREATOR = register(AreaCreatorItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), "area_creator");
    public static final Item SPAWNPOINT_SETTER = register(SpawnpointSetterItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), "spawnpoint_setter");

    private static Item register(Function<Item.Properties, Item> factory, Item.Properties properties, String path) {
        var location = AreaTools.id(path);
        var key = ResourceKey.create(Registries.ITEM, location);

        return registerItem(key, factory, properties);
    }

    private static Item registerItem(final ResourceKey<Item> key, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        Item item = itemFactory.apply(properties.setId(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void register() {
    }
}
