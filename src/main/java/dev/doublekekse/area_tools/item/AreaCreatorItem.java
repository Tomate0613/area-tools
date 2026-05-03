package dev.doublekekse.area_tools.item;

import dev.doublekekse.area_tools.client.AreaToolsClient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AreaCreatorItem extends Item {
    public static BlockPos from = null;
    public static BlockPos to = null;

    public AreaCreatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    public static BlockPos getPos(Player player) {
        var hit = player.pick(6, 0, false);

        if (hit instanceof BlockHitResult blockHit) {
            return blockHit.getBlockPos();
        }

        return BlockPos.containing(hit.getLocation());
    }

    public static AABB getAABB(Player player) {
        if (AreaCreatorItem.from == null) {
            return new AABB(getPos(player));
        }

        var from = AreaCreatorItem.from;
        var to = getPos(player);
        if (AreaCreatorItem.to != null) {
            to = AreaCreatorItem.to;
        }

        var delta = Vec3.atCenterOf(to.subtract(from));
        delta = new Vec3(delta.x > 0 ? 0.5 : -0.5, delta.y > 0 ? 0.5 : -0.5, delta.z > 0 ? 0.5 : -0.5);
        var f = Vec3.atCenterOf(from).subtract(delta);
        var t = Vec3.atCenterOf(to).add(delta);

        return new AABB(f, t);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (!level.isClientSide) {
            var itemStack = player.getItemInHand(interactionHand);
            return InteractionResultHolder.consume(itemStack);
        }

        if (from == null) {
            from = getPos(player);
        } else if (to == null) {
            to = getPos(player);
        } else {
            AreaToolsClient.openChatScreen(String.format("/area create <id> box %s", toCommandString(getAABB(player))), 13, 17);

            from = null;
            to = null;
        }

        var itemStack = player.getItemInHand(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    private String toCommandString(AABB aabb) {
        return aabb.minX + " " + aabb.minY + " " + aabb.minZ + " " + aabb.maxX + " " + aabb.maxY + " " + aabb.maxZ;
    }
}
