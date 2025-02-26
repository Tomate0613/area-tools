package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.registry.AreaItemComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    void destroyBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        var player = minecraft.player;
        assert player != null;

        var itemStack = player.getMainHandItem();
        var components = itemStack.getComponents();

        if (!components.has(AreaItemComponents.CAN_USE_IN_AREA)) {
            return;
        }

        var component = components.get(AreaItemComponents.CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(minecraft.level, blockPos.getCenter())) {
            cir.setReturnValue(false);
        }
    }
}
