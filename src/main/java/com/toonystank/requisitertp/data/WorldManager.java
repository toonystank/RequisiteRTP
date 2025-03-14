package com.toonystank.requisitertp.data;

import com.toonystank.requisitertp.RequisiteRTP;
import lombok.Getter;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class WorldManager {

    private final Map<String,RequisiteWorld> worlds = new HashMap<>();
    
    public WorldManager() {
        init();
    }
    
    public void init() {
        for (World world : RequisiteRTP.getInstance().getServer().getWorlds()) {
            worlds.put(world.getName(), new RequisiteWorld(world.getName(), world.getUID()));
        }
    }

    public RequisiteWorld getWorld(String worldName){
        return worlds.get(worldName);
    }

    public RequisiteWorld getWorld(UUID uuid) {
        for (RequisiteWorld world : worlds.values()) {
            if (world.getWorldUUID().equals(uuid)) {
                return world;
            }
        }
        return null;
    }
    
    
}
