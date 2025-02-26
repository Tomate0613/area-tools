package dev.doublekekse.area_tools.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.AreaLib;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class AreaComponent {
    public static final Codec<AreaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("area_id").forGetter(AreaComponent::areaId)
    ).apply(instance, AreaComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AreaComponent> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, AreaComponent::areaId,
        AreaComponent::new
    );
    private final ResourceLocation areaId;

    public AreaComponent(ResourceLocation areaId) {
        this.areaId = areaId;
    }

    private Area cachedArea = null;

    public boolean isInArea(Level level, Vec3 pos) {
        if (cachedArea == null) {
            cachedArea = AreaLib.getSavedData(level).get(areaId);

            if (cachedArea == null) {
                return false;
            }
        }

        return cachedArea.contains(level, pos);
    }

    public boolean isInArea(Entity entity) {
        return isInArea(entity.level(), entity.position());
    }

    public ResourceLocation areaId() {
        return areaId;
    }

    public void addToTooltip(String type, boolean isCreative, Consumer<Component> consumer) {
        if (isCreative) {
            consumer.accept(Component.translatable("item.area_tools.area_component." + type + ".creative", areaId.toString()).withStyle(ChatFormatting.GRAY));
        } else {
            consumer.accept(Component.translatable("item.area_tools.area_component." + type).withStyle(ChatFormatting.GRAY));
        }
    }
}
