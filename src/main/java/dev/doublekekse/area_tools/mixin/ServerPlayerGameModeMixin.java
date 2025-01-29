package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    @Shadow
    protected ServerLevel level;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    void destroyBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        var itemStack = player.getMainHandItem();
        var components = itemStack.getComponents();

        if (!components.has(AreaComponents.CAN_USE_IN_AREA)) {
            return;
        }

        var component = components.get(AreaComponents.CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(level, blockPos.getCenter())) {
            cir.setReturnValue(false);
        }
    }
}
