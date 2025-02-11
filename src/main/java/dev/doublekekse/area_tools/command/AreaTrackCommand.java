package dev.doublekekse.area_tools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.doublekekse.area_lib.command.argument.AreaArgument;
import dev.doublekekse.area_tools.data.AreaToolsSavedData;
import dev.doublekekse.area_tools.data.TrackedAreaItem;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class AreaTrackCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var base = literal("area_track");

        trackEvent("on_enter", base, (track) -> track.onEnter);
        trackEvent("on_exit", base, (track) -> track.onExit);

        dispatcher.register(
            base.then(literal("list").executes(ctx -> {
                var data = AreaToolsSavedData.getServerData(ctx.getSource().getServer());

                for (var item : data.trackedAreas.listAllItems()) {
                    ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(item)), false);
                }
                return 1;
            })).then(literal("spawnpoint").then(argument("area", AreaArgument.area()).then(argument("position", Vec3Argument.vec3()).executes(ctx -> {
                var area = AreaArgument.getArea(ctx, "area");
                var position = Vec3Argument.getVec3(ctx, "position");

                var data = AreaToolsSavedData.getServerData(ctx.getSource().getServer());
                var trackedAreas = data.trackedAreas;
                var item = trackedAreas.getOrCreate(area.getKey());

                item.respawnPoint = position;
                ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_track.spawnpoint"), false);

                return 1;
            }).then(argument("yaw", FloatArgumentType.floatArg()).executes(ctx -> {
                var area = AreaArgument.getArea(ctx, "area");
                var position = Vec3Argument.getVec3(ctx, "position");
                var yaw = FloatArgumentType.getFloat(ctx, "yaw");

                var data = AreaToolsSavedData.getServerData(ctx.getSource().getServer());
                var trackedAreas = data.trackedAreas;
                var item = trackedAreas.getOrCreate(area.getKey());

                item.respawnPoint = position;
                item.respawnYaw = yaw;
                ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_track.spawnpoint"), false);

                return 1;
            }))).then(literal("clear").executes(ctx -> {
                var area = AreaArgument.getArea(ctx, "area");

                var data = AreaToolsSavedData.getServerData(ctx.getSource().getServer());
                var trackedAreas = data.trackedAreas;
                var item = trackedAreas.get(area.getKey());

                if(item.isEmpty()) {
                    ctx.getSource().sendFailure(Component.translatable("commands.area_tools.area_track.track_event.list.not_tracked"));
                    return 0;
                }

                item.get().respawnPoint = null;
                ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_track.spawnpoint.clear"), false);

                if (item.get().isEmpty()) {
                    trackedAreas.remove(item.get());
                }

                data.setDirty();

                return 1;
            }))))
        );
    }

    static void trackEvent(String trackEvent, LiteralArgumentBuilder<CommandSourceStack> base, Function<TrackedAreaItem, List<String>> lookup) {
        base.then(literal(trackEvent).then(literal("add").then(argument("area", AreaArgument.area()).then(argument("command", StringArgumentType.greedyString()).executes(ctx -> {
            var area = AreaArgument.getArea(ctx, "area");
            var command = StringArgumentType.getString(ctx, "command");
            var data = AreaToolsSavedData.getServerData(ctx.getSource().getServer());
            var trackedAreas = data.trackedAreas;

            var trackItem = trackedAreas.getOrCreate(area.getKey());

            lookup.apply(trackItem).add(command);
            data.setDirty();

            ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_track." + trackEvent + ".add", command), false);

            return 1;
        })))).then(literal("remove").then(argument("area", AreaArgument.area()).then(argument("command", StringArgumentType.greedyString()).executes(ctx -> {
            var area = AreaArgument.getAreaId(ctx, "area");
            var command = StringArgumentType.getString(ctx, "command");
            var data = AreaToolsSavedData.getServerData(ctx.getSource().getServer());
            var trackedAreas = data.trackedAreas;

            var trackItem = trackedAreas.get(area);
            if (trackItem.isEmpty()) {
                ctx.getSource().sendFailure(Component.translatable("commands.area_tools.area_track.track_event.list.not_tracked"));
                return 0;
            }

            lookup.apply(trackItem.get()).remove(command);
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_track." + trackEvent + ".remove", command), false);

            if (trackItem.get().isEmpty()) {
                trackedAreas.remove(trackItem.get());
            }

            data.setDirty();

            return 1;
        })))).then(literal("list").then(argument("area", AreaArgument.area()).executes(ctx -> {
            var area = AreaArgument.getAreaId(ctx, "area");
            var data = AreaToolsSavedData.getServerData(ctx.getSource().getServer());
            var trackedAreas = data.trackedAreas;


            var trackItem = trackedAreas.get(area);
            if (trackItem.isEmpty()) {
                ctx.getSource().sendFailure(Component.translatable("commands.area_tools.area_track.track_event.list.not_tracked"));
                return 0;
            }

            var item = trackItem.get();
            var commands = lookup.apply(item);
            System.out.println(commands);

            for (var command : commands) {
                var style = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/area_track on_enter remove %s %s", area.toString(), command))).withColor(ChatFormatting.AQUA);
                ctx.getSource().sendSuccess(() -> Component.literal(command).append(" ").append(Component.translatable("commands.area_tools.area_track.track_event.list.remove").withStyle(style)), false);
            }

            return 1;
        }))));
    }
}
