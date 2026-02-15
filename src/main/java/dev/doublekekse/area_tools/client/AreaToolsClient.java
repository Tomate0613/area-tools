package dev.doublekekse.area_tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.duck.ChatScreenDuck;
import dev.doublekekse.area_tools.item.AreaCreatorItem;
import dev.doublekekse.area_tools.registry.AreaComponents;
import dev.doublekekse.area_tools.registry.AreaItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AreaToolsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            var player = Minecraft.getInstance().player;

            if (player == null) {
                return;
            }

            if (!player.canUseGameMasterBlocks()) {
                return;
            }

            var poseStack = context.matrices();

            if (poseStack == null) {
                return;
            }

            if (player.getMainHandItem().is(AreaItems.AREA_CREATOR)) {
                renderAreaCreator(context, poseStack, player);
            }

            if (player.getMainHandItem().is(AreaItems.SPAWNPOINT_SETTER)) {
                renderSpawnpointSetter(context, poseStack, player.level());
            }
        });
    }

    private static void renderSpawnpointSetter(WorldRenderContext context, PoseStack poseStack, Level level) {
        poseStack.pushPose();

        var cPos = context.worldState().cameraRenderState.pos;
        poseStack.translate(-cPos.x, -cPos.y, -cPos.z);

        for (var area : AreaLib.getSavedData(level).getAreas()) {
            var component = area.get(AreaComponents.RESPAWN_POINT_COMPONENT);

            if (component == null) {
                continue;
            }

            var pos = component.respawnPoint.add(0, 0.5, 0);
            var size = new Vec3(0.2, 0.2, 0.2);
            Gizmos.cuboid(new AABB(pos.subtract(size), pos.add(size)), new GizmoStyle(0xff22ff22, 2.5f, 0x8822ff22)).setAlwaysOnTop();
            //Gizmos.billboardText("Spawnpoint " + area.toString(), pos.add(0, 0.5, 0), TextGizmo.Style.whiteAndCentered()).setAlwaysOnTop();
        }

        poseStack.popPose();
    }

    private static void renderAreaCreator(WorldRenderContext context, PoseStack poseStack, Player player) {
        poseStack.pushPose();

        var cPos = context.worldState().cameraRenderState.pos;
        poseStack.translate(-cPos.x, -cPos.y, -cPos.z);

        Gizmos.cuboid(AreaCreatorItem.getAABB(player), GizmoStyle.stroke(-1));
        poseStack.popPose();
    }

    public static void openChatScreen(String initial, int selectFrom, int selectTo) {
        Minecraft.getInstance().execute(() -> {
            var screen = new ChatScreen(initial, false);
            Minecraft.getInstance().setScreen(screen);

            ((ChatScreenDuck) screen).area_tools$setCursorPosition(selectFrom, selectTo);
        });
    }
}
