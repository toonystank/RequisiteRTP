package com.toonystank.requisitertp;

import com.toonystank.requisitertp.data.RequisiteWorld;
import com.toonystank.requisitertp.data.WorldManager;
import org.bukkit.entity.Player;

public class RTP {

    private WorldManager worldManager;

    public boolean randomTeleport(Player player, String worldName) {
        RequisiteWorld world = worldManager.getWorld(worldName);
        if (world == null) {
            return false;
        }
        int minX = RequisiteRTP.getInstance().getMainConfig().getMaximumX();
        int minZ = RequisiteRTP.getInstance().getMainConfig().getMaximumZ();
        int maxX = RequisiteRTP.getInstance().getMainConfig().getMinimumX();
        int maxZ = RequisiteRTP.getInstance().getMainConfig().getMinimumZ();
        int x = (int) (Math.random() * (maxX - minX + 1) + minX);
        int z = (int) (Math.random() * (maxZ - minZ + 1) + minZ);
        int y = world.getWorld().getHighestBlockYAt(x, z);
        if (world.isInsideBorder(x,z)) {
            player.teleport(world.getWorld().getHighestBlockAt(x, z).getLocation());
            return true;
        }
        return false;
    }
}
