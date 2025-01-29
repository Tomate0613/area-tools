package dev.doublekekse.area_tools.registry;

import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.item.AreaCreatorItem;
import dev.doublekekse.area_tools.item.SpawnpointItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class AreaItems {
    public static final Item AREA_CREATOR = register(AreaCreatorItem::new, new Item.Properties(), "area_creator");
    public static final Item SPAWNPOINT = register(SpawnpointItem::new, new Item.Properties(), "spawnpoint");

    private static Item register(Function<Item.Properties, Item> factory, Item.Properties properties, String path) {
        ResourceLocation itemLocation = AreaTools.id(path);
        final var item = factory.apply(properties);

        return Items.registerItem(itemLocation, item);
    }

    public static void register() {
    }
}
