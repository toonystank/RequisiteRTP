package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class RTPManager {

    private final Random random;
    private final RTPQueue rtpQueue;
    private final EffectManager effectManager;
    private final List<RTPProtectionHook> protectionHooks;

    public RTPManager() {
        this.random = new Random();
        this.rtpQueue = new RTPQueue(this);
        this.protectionHooks = new ArrayList<>();
        try {
            this.effectManager = new EffectManager();
        } catch (IOException e) {
            MessageUtils.error("An error happened when loading effects.yml " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addToQueue(Player player) {
        MessageUtils.toConsole("added player " + player + " to teleport queue",true);
        rtpQueue.addPlayer(player);
    }

    public void registerProtectionHook(RTPProtectionHook hook) {
        protectionHooks.add(hook);
    }

    public Location findSafeLocation(World world) {
        MessageUtils.toConsole("Finding safe location in " + world.getName(), true);

        int minimumX = RequisiteRTP.getInstance().getMainConfig().getMinimumX();
        int maximumX = RequisiteRTP.getInstance().getMainConfig().getMaximumX();
        int minimumZ = RequisiteRTP.getInstance().getMainConfig().getMinimumZ();
        int maximumZ = RequisiteRTP.getInstance().getMainConfig().getMaximumZ();

        int attempts = 50; // Maximum attempts to find a safe spot

        for (int i = 0; i < attempts; i++) {
            int x = random.nextInt((maximumX - minimumX) + 1) + minimumX;
            int z = random.nextInt((maximumZ - minimumZ) + 1) + minimumZ;

            Location location = new Location(world, x, world.getHighestBlockYAt(x, z), z);
            MessageUtils.toConsole("Location finder attempt " + i + " | Location: " + location, true);

            if (isSafeLocation(location) && isLocationAllowed(location)) {
                MessageUtils.toConsole("Location is safe and allowed", true);
                return location;
            }
        }
        return null; // No safe location found after max attempts
    }

    private boolean isSafeLocation(Location location) {
        Block block = location.getBlock();
        Block above = location.clone().add(0, 1, 0).getBlock();
        Block below = location.clone().add(0, -1, 0).getBlock();

        return below.getType().isSolid() && !block.getType().isSolid() && !above.getType().isSolid()
                && below.getType() != Material.LAVA && below.getType() != Material.WATER;
    }

    private boolean isLocationAllowed(Location location) {
        for (RTPProtectionHook hook : protectionHooks) {
            if (!hook.isAllowed(location)) {
                return false;
            }
        }
        return true;
    }
}