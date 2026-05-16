package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.registry.AreaComponents;
import dev.doublekekse.area_tools.registry.AreaItemComponents;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar {
    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract @NotNull ItemStack getWeaponItem();

    @Shadow
    public abstract boolean mayBuild();

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    void attack(Entity entity, CallbackInfo ci) {
        var itemStack = getWeaponItem();
        var components = itemStack.getComponents();

        if (this.mayBuild() || !components.has(AreaItemComponents.CAN_USE_IN_AREA)) {
            return;
        }

        var component = components.get(AreaItemComponents.CAN_USE_IN_AREA);
        assert component != null;

        if (!component.isInArea(entity)) {
            ci.cancel();
        }
    }

    @WrapMethod(method = "isPickable")
    boolean isPickable(Operation<Boolean> original) {
        var old = original.call();
        if (!old) {
            return false;
        }

        var savedData = AreaLib.getSavedData(level());
        return !savedData.isInEntityTrackedAreaWith(AreaComponents.NO_PLAYER_PICK, this);
    }
}
