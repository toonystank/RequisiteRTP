package com.toonystank.requisitertp.hooks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Setter
@Getter
public abstract class Hook {

    private HookData hookData;

    public Hook(HookData hookData) {
        this.hookData = hookData;
    }

    public abstract boolean isAllowed(Player player, Location location);

    @AllArgsConstructor
    @Getter
    @Setter
    public static class HookData {
        private String name;
        private boolean enabled;
        private String bypassPermission;
    }
}
