package dev.doublekekse.area_tools.component.area;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class RespawnPointComponent implements AreaDataComponent {
    public Vec3 respawnPoint;
    public float respawnYaw;

    public RespawnPointComponent() {

    }

    public RespawnPointComponent(Vec3 respawnPoint, float respawnYaw) {
        this.respawnPoint = respawnPoint;
        this.respawnYaw = respawnYaw;
    }

    @Override
    public void load(AreaSavedData areaSavedData, CompoundTag tag) {
        respawnPoint = toVec3(tag.getCompound("respawn_point"));
        respawnYaw = tag.getFloat("respawn_yaw");
    }

    @Override
    public CompoundTag save() {
        var tag = new CompoundTag();

        tag.put("respawn_point", toTag(respawnPoint));
        tag.putFloat("respawn_yaw", respawnYaw);

        return tag;
    }

    private CompoundTag toTag(Vec3 pos) {
        var tag = new CompoundTag();

        tag.putDouble("x", pos.x);
        tag.putDouble("y", pos.y);
        tag.putDouble("z", pos.z);

        return tag;
    }

    private Vec3 toVec3(CompoundTag tag) {
        return new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }
}
