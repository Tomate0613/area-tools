package dev.doublekekse.area_tools.component.area;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class RespawnPointComponent implements AreaDataComponent {
    public Vec3 respawnPoint;
    public float respawnYaw;
    // TODO Make this optional rather than setting it to false when not specified in the command
    public boolean skipDeathScreen;

    public RespawnPointComponent() {

    }

    public RespawnPointComponent(Vec3 respawnPoint, float respawnYaw, boolean skipDeathScreen) {
        this.respawnPoint = respawnPoint;
        this.respawnYaw = respawnYaw;
        this.skipDeathScreen = skipDeathScreen;
    }

    @Override
    public void load(AreaSavedData areaSavedData, CompoundTag tag) {
        respawnPoint = toVec3(tag.getCompound("respawn_point").get());
        respawnYaw = tag.getFloat("respawn_yaw").orElse(0f);
        skipDeathScreen = tag.getBooleanOr("skip_death_screen", false);
    }

    @Override
    public CompoundTag save() {
        var tag = new CompoundTag();

        tag.put("respawn_point", toTag(respawnPoint));
        tag.putFloat("respawn_yaw", respawnYaw);
        tag.putBoolean("skip_death_screen", skipDeathScreen);

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
        return new Vec3(tag.getDouble("x").orElse(0.0), tag.getDouble("y").orElse(0.0), tag.getDouble("z").orElse(0.0));
    }
}
