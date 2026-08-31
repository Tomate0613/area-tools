package dev.doublekekse.area_tools.item;

import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_tools.AreaTools;
import dev.doublekekse.area_tools.client.AreaToolsClient;
import dev.doublekekse.area_tools.command.AreaToolsCommand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SpawnpointSetterItem extends Item {
    public SpawnpointSetterItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack itemStack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        var level = useOnContext.getLevel();

        if (level.isClientSide()) {
            var position = useOnContext.getClickedPos();
            var data = AreaClientData.getClientLevelData();
            var spawnpointPos = position.offset(useOnContext.getClickedFace().getUnitVec3i());

            var area = data.findAllAreasContaining(level, Vec3.atCenterOf(spawnpointPos)).stream().min(AreaTools.smallestArea());
            var areaId = area.map(value -> value.getId().toString()).orElse("<area>");
            AreaToolsClient.openChatScreen(String.format("/%s spawnpoint %s %s %s %s", AreaToolsCommand.AREA_NAME, areaId, spawnpointPos.getX(), spawnpointPos.getY(), spawnpointPos.getZ()), 23, 23 + areaId.length());
        }

        return InteractionResult.CONSUME;
    }
}
