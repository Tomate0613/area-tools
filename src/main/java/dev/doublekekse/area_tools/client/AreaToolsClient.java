package dev.doublekekse.area_tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.duck.ChatScreenDuck;
import dev.doublekekse.area_tools.item.AreaCreatorItem;
import dev.doublekekse.area_tools.registry.AreaComponents;
import dev.doublekekse.area_tools.registry.AreaItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.entity.player.Player;
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

            var poseStack = context.matrixStack();

            if (poseStack == null) {
                return;
            }

            if (player.getMainHandItem().is(AreaItems.AREA_CREATOR)) {
                renderAreaCreator(context, poseStack, player);
            }

            if (player.getMainHandItem().is(AreaItems.SPAWNPOINT_SETTER)) {
                renderSpawnpointSetter(context, poseStack);
            }
        });
    }

    private static void renderSpawnpointSetter(WorldRenderContext context, PoseStack poseStack) {
        poseStack.pushPose();

        var cPos = context.camera().getPosition();
        poseStack.translate(-cPos.x, -cPos.y, -cPos.z);

        for (var area : AreaLib.getSavedData(context.world()).getAreas()) {
            var component = area.get(AreaComponents.RESPAWN_POINT_COMPONENT);

            if (component == null) {
                continue;
            }

            var pos = component.respawnPoint.add(0, 0.5, 0);
            var size = new Vec3(0.2, 0.2 ,0.2);
            ShapeRenderer.renderLineBox(poseStack, context.consumers().getBuffer(RenderType.lines()), new AABB(pos.subtract(size), pos.add(size)), 0.2f, 1, 0.2f, 1);
        }

        poseStack.popPose();
    }

    private static void renderAreaCreator(WorldRenderContext context, PoseStack poseStack, Player player) {
        poseStack.pushPose();

        var cPos = context.camera().getPosition();
        poseStack.translate(-cPos.x, -cPos.y, -cPos.z);

        ShapeRenderer.renderLineBox(poseStack, context.consumers().getBuffer(RenderType.lines()), AreaCreatorItem.getAABB(player), 1, 1, 1, 1);
        poseStack.popPose();
    }

    public static void openChatScreen(String initial, int selectFrom, int selectTo) {
        Minecraft.getInstance().execute(() -> {
            var screen = new ChatScreen(initial);
            Minecraft.getInstance().setScreen(screen);

            ((ChatScreenDuck) screen).area_tools$setCursorPosition(selectFrom, selectTo);
        });
    }
}
