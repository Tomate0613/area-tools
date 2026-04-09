package dev.doublekekse.area_tools.component.area;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.phys.Vec3;

public class RespawnPointComponent {
    public Vec3 respawnPoint;
    public float respawnYaw;
    // TODO Make this optional rather than setting it to false when not specified in the command
    public boolean skipDeathScreen;

    public static Codec<RespawnPointComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.fieldOf("respawn_point").forGetter(v -> v.respawnPoint),
        Codec.FLOAT.fieldOf("respawn_yaw").forGetter(v -> v.respawnYaw),
        Codec.BOOL.fieldOf("skip_death_screen").forGetter(v -> v.skipDeathScreen)
    ).apply(instance, RespawnPointComponent::new));

    public RespawnPointComponent(Vec3 respawnPoint, float respawnYaw, boolean skipDeathScreen) {
        this.respawnPoint = respawnPoint;
        this.respawnYaw = respawnYaw;
        this.skipDeathScreen = skipDeathScreen;
    }
}
