package dev.doublekekse.area_tools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.doublekekse.area_lib.command.argument.AreaArgument;
import dev.doublekekse.area_tools.component.area.EventsComponent;
import dev.doublekekse.area_tools.component.area.RespawnPointComponent;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class AreaToolsCommand {
    public static final String AREA_NAME = "area_tools";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var base = literal(AREA_NAME).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));

        trackEvent("on_enter", base, (track) -> track.onEnter);
        trackEvent("on_exit", base, (track) -> track.onExit);

        dispatcher.register(
            base.then(literal("spawnpoint").then(argument("area", IdentifierArgument.id()).suggests(AreaArgument::listSuggestions).then(argument("position", Vec3Argument.vec3()).executes(ctx -> {
                var position = Vec3Argument.getVec3(ctx, "position");

                return spawnpoint(ctx, new RespawnPointComponent(position, 0, false));
            }).then(argument("yaw", FloatArgumentType.floatArg()).executes(ctx -> {
                var position = Vec3Argument.getVec3(ctx, "position");
                var yaw = FloatArgumentType.getFloat(ctx, "yaw");

                return spawnpoint(ctx, new RespawnPointComponent(position, yaw, false));
            }).then(argument("skip_death_screen", BoolArgumentType.bool()).executes(ctx -> {
                var position = Vec3Argument.getVec3(ctx, "position");
                var yaw = FloatArgumentType.getFloat(ctx, "yaw");
                var skipDeathScreen = BoolArgumentType.getBool(ctx, "skip_death_screen");

                return spawnpoint(ctx, new RespawnPointComponent(position, yaw, skipDeathScreen));
            })))).then(literal("clear").executes(ctx -> {
                var server = ctx.getSource().getServer();
                var area = AreaArgument.getArea(ctx, "area");

                area.remove(server, AreaComponents.RESPAWN_POINT_COMPONENT);

                ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_tools.spawnpoint.clear"), false);

                return 1;
            }))))
        );
    }

    private static int spawnpoint(CommandContext<CommandSourceStack> ctx, RespawnPointComponent component) throws CommandSyntaxException {
        var server = ctx.getSource().getServer();
        var area = AreaArgument.getArea(ctx, "area");

        area.put(server, AreaComponents.RESPAWN_POINT_COMPONENT, component);

        ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_tools.spawnpoint"), false);

        return 1;
    }

    static void trackEvent(String trackEvent, LiteralArgumentBuilder<CommandSourceStack> base, Function<EventsComponent, List<String>> lookup) {
        base.then(literal(trackEvent).then(literal("add").then(argument("area", IdentifierArgument.id()).suggests(AreaArgument::listSuggestions).then(argument("command", StringArgumentType.greedyString()).executes(ctx -> {
            var server = ctx.getSource().getServer();
            var area = AreaArgument.getArea(ctx, "area");
            var command = StringArgumentType.getString(ctx, "command");
            var component = area.getOrDefault(AreaComponents.EVENTS_COMPONENT, new EventsComponent());

            lookup.apply(component).add(command);
            area.put(server, AreaComponents.EVENTS_COMPONENT, component);

            ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_tools." + trackEvent + ".add", command), false);

            return 1;
        })))).then(literal("remove").then(argument("area", IdentifierArgument.id()).suggests(AreaArgument::listSuggestions).then(argument("command", StringArgumentType.greedyString()).executes(ctx -> {
            var server = ctx.getSource().getServer();
            var area = AreaArgument.getArea(ctx, "area");
            var command = StringArgumentType.getString(ctx, "command");

            var component = area.get(AreaComponents.EVENTS_COMPONENT);

            if (component == null) {
                ctx.getSource().sendFailure(Component.translatable("commands.area_tools.area_tools.track_event.no_events"));
                return 0;
            }

            lookup.apply(component).remove(command);
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.area_tools.area_tools." + trackEvent + ".remove", command), false);

            if (component.isEmpty()) {
                area.remove(server, AreaComponents.EVENTS_COMPONENT);
            }

            return 1;
        })))).then(literal("list").then(argument("area", IdentifierArgument.id()).suggests(AreaArgument::listSuggestions).executes(ctx -> {
            var area = AreaArgument.getArea(ctx, "area");
            var component = area.get(AreaComponents.EVENTS_COMPONENT);


            if (component == null) {
                ctx.getSource().sendFailure(Component.translatable("commands.area_tools.area_tools.track_event.no_events"));
                return 0;
            }

            var commands = lookup.apply(component);

            for (var command : commands) {
                var style = Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand(String.format("/%s %s remove %s %s", AREA_NAME, trackEvent, area.getId(), command))).withColor(ChatFormatting.AQUA);
                ctx.getSource().sendSuccess(() -> Component.literal(command).append(" ").append(Component.translatable("commands.area_tools.area_tools.track_event.list.remove").withStyle(style)), false);
            }

            return 1;
        }))));
    }
}
