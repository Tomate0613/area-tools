package dev.doublekekse.area_tools;

import dev.doublekekse.area_tools.command.AreaToolsCommand;
import dev.doublekekse.area_tools.registry.AreaComponents;
import dev.doublekekse.area_tools.registry.AreaItemComponents;
import dev.doublekekse.area_tools.registry.AreaItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.List;

public class AreaTools implements ModInitializer {
    @Override
    public void onInitialize() {
        AreaItems.register();
        AreaItemComponents.register();
        AreaComponents.register();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(content -> {
            content.accept(AreaItems.AREA_CREATOR);
            content.accept(AreaItems.SPAWNPOINT_SETTER);
        });

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                AreaToolsCommand.register(dispatcher);
            }
        );
    }

    public static void runCommands(MinecraftServer server, Player player, List<String> commands) {
        var stack = player.createCommandSourceStack().withSuppressedOutput().withMaximumPermission(2);

        for (var command : commands) {
            server.getCommands().performPrefixedCommand(stack, command);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("area_tools", path);
    }
}
