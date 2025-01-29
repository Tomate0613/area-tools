package dev.doublekekse.area_tools.item;

import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_lib.data.AreaSavedData;
import dev.doublekekse.area_tools.client.AreaToolsClient;
import dev.doublekekse.area_tools.data.AreaToolsSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SpawnpointItem extends Item {
    public SpawnpointItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        var level = useOnContext.getLevel();

        if (level.isClientSide) {
            var position = useOnContext.getClickedPos();
            var data = AreaClientData.getClientLevelData();
            var spawnpointPos = position.above();

            var entry = data.find(level, position.getCenter());
            if (entry != null) {
                var areaId = entry.getKey().toString();
                AreaToolsClient.openChatScreen(String.format("/area_track on_enter add %s spawnpoint @s %s %s %s", areaId, spawnpointPos.getX(), spawnpointPos.getY(), spawnpointPos.getZ()), 25, 25 + areaId.length());
            }
        }

        return InteractionResult.CONSUME;
    }
}
