package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @WrapOperation(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    <T extends ParticleOptions> int addFallParticles(ServerLevel instance, T particle, double x, double y, double z, int count, double xDist, double yDist, double zDist, double speed, Operation<Integer> original) {
        if (is(EntityType.PLAYER)) {
            var data = AreaLib.getSavedData(level());
            var noParticles = data.isInEntityTrackedAreaWith(AreaComponents.NO_PLAYER_PARTICLES, this);

            if (noParticles) {
                return 0;
            }
        }

        return original.call(instance, particle, x, y, z, count, xDist, yDist, zDist, speed);
    }
}
