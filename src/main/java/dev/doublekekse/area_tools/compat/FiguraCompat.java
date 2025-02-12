package dev.doublekekse.area_tools.compat;

import org.figuramc.figura.avatar.AvatarManager;

public class FiguraCompat {
    public static boolean isPanic() {
        return AvatarManager.panic;
    }

    public static void setPanic(boolean panic) {
        if (AvatarManager.panic != panic) {
            AvatarManager.togglePanic();
        }
    }
}
