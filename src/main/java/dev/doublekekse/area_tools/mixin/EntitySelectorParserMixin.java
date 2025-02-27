package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.duck.EntitySelectorDuck;
import dev.doublekekse.area_tools.duck.EntitySelectorParserDuck;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySelectorParser.class)
public class EntitySelectorParserMixin implements EntitySelectorParserDuck {
    @Unique
    ResourceLocation area;

    @Override
    public ResourceLocation map_utils$getArea() {
        return area;
    }

    @Override
    public void map_utils$setArea(ResourceLocation id) {
        area = id;
    }

    @Inject(method = "getSelector", at = @At("RETURN"))
    void getSelector(CallbackInfoReturnable<EntitySelector> cir) {
        var selector = cir.getReturnValue();

        ((EntitySelectorDuck) selector).area_tools$setArea(area);
    }
}
