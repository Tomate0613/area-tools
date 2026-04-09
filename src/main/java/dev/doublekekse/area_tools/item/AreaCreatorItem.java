package dev.doublekekse.area_tools.item;

import dev.doublekekse.area_lib.gizmos.SphereGizmo;
import dev.doublekekse.area_tools.client.AreaToolsClient;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AreaCreatorItem extends Item {
    private static final List<BlockPos> positions = new ArrayList<>();
    private static Mode mode = Mode.BOX;

    enum Mode {
        BOX("box"),
        SPHERE("sphere");

        public final String commandName;

        Mode(String commandName) {
            this.commandName = commandName;
        }

        Mode next() {
            return switch (this) {
                case BOX -> SPHERE;
                case SPHERE -> BOX;
            };
        }
    }

    public AreaCreatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack itemStack) {
        return true;
    }

    public static BlockPos getPos(Player player) {
        var hit = player.pick(6, 0, false);

        if (hit instanceof BlockHitResult blockHit) {
            return blockHit.getBlockPos();
        }

        return BlockPos.containing(hit.getLocation());
    }

    private static AABB getBoxAABB(Player player) {
        if (positions.isEmpty()) {
            return new AABB(getPos(player));
        }

        if (positions.size() == 1) {
            return AABB.encapsulatingFullBlocks(positions.getFirst(), getPos(player));
        }

        return AABB.encapsulatingFullBlocks(positions.get(0), positions.get(1));
    }

    private static Tuple<Vec3, Double> getSphere(Player player) {
        var firstPos = getPos(player).getCenter();
        var secondPos = getPos(player).getCenter();

        if (!positions.isEmpty()) {
            firstPos = positions.getFirst().getCenter();
        }

        if (positions.size() > 1) {
            secondPos = positions.get(1).getCenter();
        }

        if (firstPos.equals(secondPos)) {
            secondPos = secondPos.add(0, 0, 1);
        }

        return new Tuple<>(firstPos, secondPos.distanceTo(firstPos));
    }

    @Override
    public @NonNull InteractionResult use(Level level, @NonNull Player player, @NonNull InteractionHand interactionHand) {
        if (!level.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        if (player.isShiftKeyDown()) {
            if (positions.isEmpty()) {
                mode = mode.next();
            } else {
                positions.clear();
            }
            return InteractionResult.SUCCESS;
        }

        if (positions.size() < 2) {
            positions.add(getPos(player));
        } else {
            create(player);
            positions.clear();
        }

        return InteractionResult.SUCCESS;
    }

    private void create(Player player) {
        AreaToolsClient.openChatScreen(String.format("/area create <id> %s %s", mode.commandName, commandString(player)), 13, 17);
    }

    private String commandString(Player player) {
        return switch (mode) {
            case BOX -> toCommandString(getBoxAABB(player));
            case SPHERE -> toCommandString(getSphere(player));
        };
    }

    private String toCommandString(AABB aabb) {
        return aabb.minX + " " + aabb.minY + " " + aabb.minZ + " " + aabb.maxX + " " + aabb.maxY + " " + aabb.maxZ;
    }

    private String toCommandString(Tuple<Vec3, Double> sphere) {
        var pos = sphere.getA();
        return pos.x + " " + pos.y + " " + pos.z + " " + sphere.getB();
    }

    public static void renderGizmo(Player player) {
        var style = GizmoStyle.stroke(-1);

        switch (mode) {
            case BOX -> {
                Gizmos.cuboid(getBoxAABB(player), style);
            }
            case SPHERE -> {
                var sphere = getSphere(player);
                Gizmos.addGizmo(new SphereGizmo(sphere.getA(), sphere.getB(), style));
            }
        }
    }
}
