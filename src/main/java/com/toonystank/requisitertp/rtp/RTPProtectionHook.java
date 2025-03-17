package com.toonystank.requisitertp.rtp;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public abstract class RTPProtectionHook {

    private final String name;
    private final boolean enabled;

    public RTPProtectionHook(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public abstract boolean isAllowed(Location location);
}
