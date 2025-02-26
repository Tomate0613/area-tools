package dev.doublekekse.area_tools.component.area;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.nbt.CompoundTag;

public class AreaEventsComponent implements AreaDataComponent {
    @Override
    public void load(AreaSavedData areaSavedData, CompoundTag compoundTag) {

    }

    @Override
    public CompoundTag save() {
        return null;
    }
}
