package dev.doublekekse.area_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_tools.registry.AreaComponents;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState>  {
	@WrapMethod(
			method = "shouldRender"
	) protected boolean shouldRenderLayers(final T entity, final Frustum culler, final double camX, final double camY, final double camZ, Operation<Boolean> original) {
		boolean result = original.call(entity, culler, camX, camY, camZ);
		if (!result) return false;
		var data = AreaClientData.getClientLevelData();
		if (data == null) return true;
		return !(entity instanceof Player player && data.isInEntityTrackedAreaWith(AreaComponents.SOLO_PLAYER_RENDER, player));
	}
}
