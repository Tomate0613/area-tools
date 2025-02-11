package dev.doublekekse.area_tools.registry;

import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.item.AreaCreatorItem;
import dev.doublekekse.area_tools.item.SpawnpointSetterItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class AreaItems {
    public static final Item AREA_CREATOR = register(AreaCreatorItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), "area_creator");
    public static final Item SPAWNPOINT_SETTER = register(SpawnpointSetterItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), "spawnpoint_setter");

    private static Item register(Function<Item.Properties, Item> factory, Item.Properties properties, String path) {
        ResourceLocation itemLocation = AreaTools.id(path);
        final var item = factory.apply(properties);

        return Items.registerItem(itemLocation, item);
    }

    public static void register() {
    }
}
