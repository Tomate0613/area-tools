package dev.doublekekse.area_tools;

import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_tools.command.AreaToolsCommand;
import dev.doublekekse.area_tools.registry.AreaComponents;
import dev.doublekekse.area_tools.registry.AreaItemComponents;
import dev.doublekekse.area_tools.registry.AreaItems;
import dev.doublekekse.area_tools.registry.AreaLootConditions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.Comparator;
import java.util.List;

public class AreaTools implements ModInitializer {
    @Override
    public void onInitialize() {
        AreaItems.register();
        AreaItemComponents.register();
        AreaComponents.register();
        AreaLootConditions.register();

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.OP_BLOCKS).register(content -> {
            content.accept(AreaItems.AREA_CREATOR);
            content.accept(AreaItems.SPAWNPOINT_SETTER);
        });

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, commandBuildContext, environment) -> {
                AreaToolsCommand.register(dispatcher, commandBuildContext);
            }
        );
    }

    public static void runCommands(MinecraftServer server, Player player, List<String> commands) {
        var stack = player.createCommandSourceStackForNameResolution((ServerLevel) player.level()).withSuppressedOutput().withMaximumPermission(LevelBasedPermissionSet.forLevel(PermissionLevel.GAMEMASTERS));

        for (var command : commands) {
            server.getCommands().performPrefixedCommand(stack, command);
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("area_tools", path);
    }

    public static Comparator<Area> smallestArea() {
        return Comparator.comparingDouble((area) -> {
            var boundingBox = area.getBoundingBox();

            if (boundingBox == null) {
                return Double.MAX_VALUE;
            }

            return boundingBox.getSize();
        });
    }
}
