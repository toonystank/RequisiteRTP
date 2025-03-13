package com.toonystank.requisitertp.data;

import com.toonystank.requisitertp.RequisiteRTP;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.UUID;

@Getter
public class RequisiteWorld {

    private final String worldName;
    private final UUID worldUUID;

    private final ReferenceWorld referenceWorld;

    public RequisiteWorld(String worldName, UUID worldUUID) {
        this.worldName = worldName;
        this.worldUUID = worldUUID;
        this.referenceWorld = new ReferenceWorld(this);
    }

    public World getWorld() {
        return referenceWorld.getWorld();
    }

    public boolean isInsideBorder(int x, int z) {
        World world = getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        double size = worldBorder.getSize();
        double centerX = worldBorder.getCenter().getX();
        double centerZ = worldBorder.getCenter().getZ();
        double minX = centerX - size / 2;
        double minZ = centerZ - size / 2;
        double maxX = centerX + size / 2;
        double maxZ = centerZ + size / 2;
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }



    @Getter
    public static class ReferenceWorld {

        protected final RequisiteWorld requisiteWorld;

        private World cachedWorld;

        public ReferenceWorld(RequisiteWorld requisiteWorld) {
            this.requisiteWorld = requisiteWorld;
        }

        public World getWorld() {
            if (cachedWorld == null) {
                cachedWorld = RequisiteRTP.getInstance().getServer().getWorld(requisiteWorld.worldUUID);
            }
            return cachedWorld;
        }
    }
}
