package dev.doublekekse.area_tools.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static dev.doublekekse.area_tools.registry.AreaItemComponents.CAN_USE_IN_AREA;
import static dev.doublekekse.area_tools.registry.AreaItemComponents.DISSOLVE;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Shadow
    public abstract void setCount(int i);

    @Inject(method = "addDetailsToTooltip", at = @At("RETURN"))
    void getTooltipLines(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Player player, TooltipFlag tooltipFlag, Consumer<Component> consumer, CallbackInfo ci) {
        var isCreative = player != null && player.isCreative();

        if (has(CAN_USE_IN_AREA)) {
            get(CAN_USE_IN_AREA).addToTooltip("can_use_in_area", isCreative, consumer);
        }
        if (has(DISSOLVE)) {
            get(DISSOLVE).addToTooltip("dissolve", isCreative, consumer);
        }
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    void tick(Level level, Entity entity, EquipmentSlot equipmentSlot, CallbackInfo ci) {
        if (!has(DISSOLVE)) {
            return;
        }

        var component = get(DISSOLVE);
        assert component != null;

        if (!component.isInArea(entity)) {
            setCount(0);
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player.mayBuild() || !has(CAN_USE_IN_AREA)) {
            return;
        }

        var component = get(CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(level, player.position())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    void useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (!has(CAN_USE_IN_AREA)) {
            return;
        }

        var player = useOnContext.getPlayer();
        if (player == null || player.mayBuild()) {
           return;
        }

        var component = get(CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(useOnContext.getLevel(), useOnContext.getClickLocation())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "mineBlock", at = @At("HEAD"), cancellable = true)
    void mineBlock(Level level, BlockState blockState, BlockPos blockPos, Player player, CallbackInfo ci) {
        if (player.mayBuild() || !has(CAN_USE_IN_AREA)) {
            return;
        }

        var component = get(CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(level, blockPos.getCenter())) {
            ci.cancel();
        }
    }
}
