package dev.doublekekse.area_tools.mixin;

import dev.doublekekse.area_tools.duck.ChatScreenDuck;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatScreen.class)
public class ChatScreenMixin implements ChatScreenDuck {

    @Shadow
    protected EditBox input;

    @Override
    public void area_tools$setCursorPosition(int from, int to) {
        input.setCursorPosition(from);
        input.setHighlightPos(to);
    }
}
