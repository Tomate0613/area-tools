package dev.doublekekse.area_tools.registry;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.component.AreaDataComponentType;
import dev.doublekekse.area_lib.registry.AreaDataComponentTypeRegistry;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.component.area.EventsComponent;
import dev.doublekekse.area_tools.component.area.RespawnPointComponent;

import java.util.function.Supplier;

public final class AreaComponents {
    public static final AreaDataComponentType<EventsComponent> EVENTS_COMPONENT = registerTracking(EventsComponent::new, "events");
    public static final AreaDataComponentType<RespawnPointComponent> RESPAWN_POINT_COMPONENT = registerTracking(RespawnPointComponent::new, "respawn_point");

    private static <T extends AreaDataComponent> AreaDataComponentType<T> registerTracking(Supplier<T> factory, String path) {
        var id = AreaTools.id(path);

        return AreaDataComponentTypeRegistry.registerTracking(id, factory);
    }

    public static void register() {

    }
}
