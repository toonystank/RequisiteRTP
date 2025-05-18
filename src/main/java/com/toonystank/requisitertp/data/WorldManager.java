package com.toonystank.requisitertp.data;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.FileConfig;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class WorldManager extends FileConfig {

    private final Map<String,RequisiteWorld> worlds = new HashMap<>();
    
    public WorldManager() throws IOException {
        super("worlds.yml",false,false);
        init();
    }
    
    private void init() throws IOException {
        for (World world : RequisiteRTP.getInstance().getServer().getWorlds()) {
            loadWorld(world);
        }
    }

    private void loadWorld(World world) throws IOException {
        if (worlds.containsKey(world.getName())) {
            return;
        }
        boolean isEnabled = getBoolean(world.getName() + ".enabled", true);
        int worldMinimumX = getInt(world.getName() + ".minimumX", 100);
        int worldMaximumX = getInt(world.getName() + ".maximumX", 10000);
        int worldMinimumZ = getInt(world.getName() + ".minimumZ", 100);
        int worldMaximumZ = getInt(world.getName() + ".maximumZ", 10000);
        worlds.put(world.getName(), new RequisiteWorld(world.getName(), world.getUID(), isEnabled, worldMinimumX, worldMaximumX, worldMinimumZ, worldMaximumZ));
    }

    public boolean isWorldEnabled(String worldName) {
        RequisiteWorld world = worlds.get(worldName);
        if (world == null) {
            return false;
        }
        return world.isEnabled();
    }
    public boolean isWorldEnabled(Player player) {
        return isWorldEnabled(player.getWorld().getName());
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
