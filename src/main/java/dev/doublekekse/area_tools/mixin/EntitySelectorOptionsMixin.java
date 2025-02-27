package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.duck.EntitySelectorParserDuck;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(EntitySelectorOptions.class)
public abstract class EntitySelectorOptionsMixin {
    @Shadow
    private static void register(String string, EntitySelectorOptions.Modifier modifier, Predicate<EntitySelectorParser> predicate, Component component) {}

    @Inject(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions;register(Ljava/lang/String;Lnet/minecraft/commands/arguments/selector/options/EntitySelectorOptions$Modifier;Ljava/util/function/Predicate;Lnet/minecraft/network/chat/Component;)V", ordinal = 0))
    private static void bootStrap(CallbackInfo ci) {
        register("area", (parser) -> {
            var id = ResourceLocation.read(parser.getReader());

            ((EntitySelectorParserDuck)parser).map_utils$setArea(id);
        }, (parser) -> ((EntitySelectorParserDuck) parser).map_utils$getArea() == null, Component.translatable("argument.area_tools.entity.options.area.description"));
    }
}
