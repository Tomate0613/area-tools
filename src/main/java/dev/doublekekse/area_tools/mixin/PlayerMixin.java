package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.registry.AreaItemComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    public abstract @NotNull ItemStack getWeaponItem();

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    void attack(Entity entity, CallbackInfo ci) {
        var itemStack = getWeaponItem();
        var components = itemStack.getComponents();

        if (!components.has(AreaItemComponents.CAN_USE_IN_AREA)) {
            return;
        }

        var component = components.get(AreaItemComponents.CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(entity)) {
            ci.cancel();
        }
    }

    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    void canHarmPlayer(Player player, CallbackInfoReturnable<Boolean> cir) {
        var savedData = AreaLib.getSavedData(player.level());
        var area = savedData.get(AreaTools.id("pvp_disabled"));

        if (area != null && area.contains(player)) {
            cir.setReturnValue(false);
        }
    }
}
