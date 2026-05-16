package dev.doublekekse.area_tools.registry;

import com.mojang.serialization.Codec;
import dev.doublekekse.area_lib.component.EntityTrackedAreaComponentType;
import dev.doublekekse.area_lib.component.SampledAreaComponentType;
import dev.doublekekse.area_lib.registry.AreaComponentRegistry;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.component.area.EnvironmentAttributesComponent;
import dev.doublekekse.area_tools.component.area.EventsComponent;
import dev.doublekekse.area_tools.component.area.RespawnPointComponent;
import net.minecraft.util.Unit;

public final class AreaComponents {
    public static final EntityTrackedAreaComponentType<EventsComponent> EVENTS_COMPONENT = registerEntityTracked("events", EventsComponent.CODEC);
    public static final EntityTrackedAreaComponentType<Unit> FIGURA_PANIC = registerEntityTracked("figura_panic", Unit.CODEC);
    public static final EntityTrackedAreaComponentType<Unit> SOLO_PLAYER_RENDER = registerEntityTracked("solo_player_render", Unit.CODEC);
    public static final EntityTrackedAreaComponentType<Unit> NO_PLAYER_PARTICLES = registerEntityTracked("no_player_particles", Unit.CODEC);
    public static final EntityTrackedAreaComponentType<Unit> NO_PUSHING = registerEntityTracked("no_pushing", Unit.CODEC);
    public static final EntityTrackedAreaComponentType<Unit> NO_PLAYER_PICK = registerEntityTracked("no_player_pick", Unit.CODEC);
    public static final EntityTrackedAreaComponentType<Unit> PVP_TOGGLED = registerEntityTracked("pvp_toggled", Unit.CODEC);

    public static final SampledAreaComponentType<RespawnPointComponent> RESPAWN_POINT_COMPONENT = registerSampled("respawn_point", RespawnPointComponent.CODEC);
    public static final SampledAreaComponentType<EnvironmentAttributesComponent> ENVIRONMENT_ATTRIBUTES_COMPONENT = registerSampled("environment_attributes", EnvironmentAttributesComponent.CODEC);

    private static <T> EntityTrackedAreaComponentType<T> registerEntityTracked(String path, Codec<T> codec) {
        var id = AreaTools.id(path);
        return AreaComponentRegistry.registerEntityTracked(id, codec);
    }

    private static <T> SampledAreaComponentType<T> registerSampled(String path, Codec<T> codec) {
        var id = AreaTools.id(path);
        return AreaComponentRegistry.registerSampled(id, codec);
    }

    public static void register() {

    }
}
