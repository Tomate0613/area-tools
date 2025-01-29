package dev.doublekekse.area_tools.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.AreaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public final class CanUseInAreaComponent {
    public static final Codec<CanUseInAreaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("area_id").forGetter(CanUseInAreaComponent::areaId)
    ).apply(instance, CanUseInAreaComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CanUseInAreaComponent> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, CanUseInAreaComponent::areaId,
        CanUseInAreaComponent::new
    );
    private final ResourceLocation areaId;

    public CanUseInAreaComponent(ResourceLocation areaId) {
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
}
