package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    @Final
    PatchedDataComponentMap components;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!components.has(AreaComponents.CAN_USE_IN_AREA)) {
            return;
        }

        var component = components.get(AreaComponents.CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(level, player.position())) {
            cir.setReturnValue(InteractionResultHolder.fail((ItemStack) (Object) this));
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    void useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (!components.has(AreaComponents.CAN_USE_IN_AREA)) {
            return;
        }

        var component = components.get(AreaComponents.CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(useOnContext.getLevel(), useOnContext.getClickLocation())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
    void mineBlock(Level level, BlockState blockState, BlockPos blockPos, Player player, CallbackInfo ci) {
        if (!components.has(AreaComponents.CAN_USE_IN_AREA)) {
            return;
        }

        System.out.println("component");

        var component = components.get(AreaComponents.CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(level, blockPos.getCenter())) {
            ci.cancel();
        }
    }
}
