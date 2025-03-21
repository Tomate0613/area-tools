package dev.doublekekse.area_tools.mixin;

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
}
