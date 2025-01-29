package dev.doublekekse.area_tools.registry;

import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.component.CanUseInAreaComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class AreaComponents {
    public static final DataComponentType<CanUseInAreaComponent> CAN_USE_IN_AREA = register(
        DataComponentType.<CanUseInAreaComponent>builder().persistent(CanUseInAreaComponent.CODEC).networkSynchronized(CanUseInAreaComponent.STREAM_CODEC).cacheEncoding().build(),
        "can_use_in_area"
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
