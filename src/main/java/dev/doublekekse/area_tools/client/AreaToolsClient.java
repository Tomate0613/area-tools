package dev.doublekekse.area_tools.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.doublekekse.area_tools.duck.ChatScreenDuck;
import dev.doublekekse.area_tools.item.AreaCreatorItem;
import dev.doublekekse.area_tools.registry.AreaItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;

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

            if (!player.getMainHandItem().is(AreaItems.AREA_CREATOR)) {
                return;
            }

            var poseStack = context.matrixStack();

            if (poseStack == null) {
                return;
            }

            poseStack.pushPose();

            var cPos = context.camera().getPosition();
            poseStack.translate(-cPos.x, -cPos.y, -cPos.z);


            LevelRenderer.renderLineBox(poseStack, context.consumers().getBuffer(RenderType.lines()), AreaCreatorItem.getAABB(player), 1, 1, 1, 1);

            poseStack.popPose();
        });
    }

    public static void openChatScreen(String initial, int selectFrom, int selectTo) {
        RenderSystem.recordRenderCall(() -> {
            var screen = new ChatScreen(initial);
            Minecraft.getInstance().setScreen(screen);

            ((ChatScreenDuck) screen).area_tools$setCursorPosition(selectFrom, selectTo);
        });
    }
}
