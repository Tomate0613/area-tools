package dev.doublekekse.area_tools.registry;

import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.component.AreaComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class AreaComponents {
    public static final DataComponentType<AreaComponent> CAN_USE_IN_AREA = register(
        DataComponentType.<AreaComponent>builder().persistent(AreaComponent.CODEC).networkSynchronized(AreaComponent.STREAM_CODEC).cacheEncoding().build(),
        "can_use_in_area"
    );
    public static final DataComponentType<AreaComponent> DISSOLVE_COMPONENT = register(
        DataComponentType.<AreaComponent>builder().persistent(AreaComponent.CODEC).networkSynchronized(AreaComponent.STREAM_CODEC).cacheEncoding().build(),
        "dissolve"
    );

    private static <A, T extends DataComponentType<A>> T register(T componentType, String path) {
        return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            AreaTools.id(path),
            componentType
        );
    }

    public static void register() {
    }
}
