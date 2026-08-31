package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_lib.AreaLib;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.core.TypedInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements TypedInstance<EntityType<?>> {
    @Shadow
    private Level level;

    @Inject(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), cancellable = true)
    void spawnSprintParticles(CallbackInfo ci) {
        if (is(EntityTypes.PLAYER)) {
            var data = AreaLib.getSavedData(level);
            var noParticles = data.isInEntityTrackedAreaWith(AreaComponents.NO_PLAYER_PARTICLES, (Entity) (Object) this);

            if (noParticles) {
                ci.cancel();
            }
        }
    }
}
