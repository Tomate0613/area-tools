package dev.doublekekse.area_tools.item;

import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_tools.client.AreaToolsClient;
import dev.doublekekse.area_tools.command.AreaToolsCommand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class SpawnpointSetterItem extends Item {
    public SpawnpointSetterItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        var level = useOnContext.getLevel();

        if (level.isClientSide) {
            var position = useOnContext.getClickedPos();
            var data = AreaClientData.getClientLevelData();
            var spawnpointPos = position.above();

            var area = data.findAllAreasContaining(level, position.getCenter());
            if (area != null) {
                var areaId = area.getId().toString();
                AreaToolsClient.openChatScreen(String.format("/%s spawnpoint %s %s %s %s", AreaToolsCommand.AREA_NAME, areaId, spawnpointPos.getX(), spawnpointPos.getY(), spawnpointPos.getZ()), 23, 23 + areaId.length());
            }
        }

        return InteractionResult.CONSUME;
    }
}
