package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.effect.EffectManager;
import com.toonystank.requisitertp.effect.implementations.SpiralEffect;
import com.toonystank.requisitertp.effect.implementations.TitleEffect;
import com.toonystank.requisitertp.hooks.Hook;
import com.toonystank.requisitertp.utils.Handlers;
import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;
import lombok.var;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Getter
public class RTPManager {

    private final Random random;
    private final RTPQueue rtpQueue;
    private final EffectManager effectManager;
    private final List<Hook> protectionHooks;

    public RTPManager() {
        this.random = new Random();
        this.rtpQueue = new RTPQueue(this);
        this.protectionHooks = new ArrayList<>();
        try {
            this.effectManager = new EffectManager();
            initializeEffects();
        } catch (IOException e) {
            MessageUtils.error("An error happened when loading effects.yml " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public void initializeEffects() {
        MessageUtils.toConsole("initializing effects..",false);
        try {
            effectManager.registerEffect("Spiral",true, Collections.singletonList("Spawns particles around player"),Collections.emptyList(), "effect.spiral", SpiralEffect.class);
            effectManager.registerEffect("Title",true, Collections.singletonList("Shows a title to the player"),Collections.emptyList(), "effect.title", TitleEffect.class);
        } catch (IOException e) {
            MessageUtils.error("An error happened when loading effect " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addToQueue(Player player) {
        MessageUtils.toConsole("added player " + player + " to teleport queue",true);
        rtpQueue.addPlayer(player);
    }

    public void registerProtectionHook(Hook hook) {
        protectionHooks.add(hook);
    }

    public Location findSafeLocation(Player player, World world) {
        MessageUtils.toConsole("Finding safe location in " + world.getName(), true);

        var mainConfig = RequisiteRTP.getInstance().getMainConfig();
        int minX = mainConfig.getWorldMinimumX();
        int maxX = mainConfig.getWorldMaximumX();
        int minZ = mainConfig.getWorldMinimumZ();
        int maxZ = mainConfig.getWorldMaximumZ();

        WorldBorder border = world.getWorldBorder();
        double borderSize = border.getSize() / 2;
        Location borderCenter = border.getCenter();

        int borderMinX = (int) (borderCenter.getX() - borderSize);
        int borderMaxX = (int) (borderCenter.getX() + borderSize);
        int borderMinZ = (int) (borderCenter.getZ() - borderSize);
        int borderMaxZ = (int) (borderCenter.getZ() + borderSize);

        int finalMinX = Math.max(minX, borderMinX);
        int finalMaxX = Math.min(maxX, borderMaxX);
        int finalMinZ = Math.max(minZ, borderMinZ);
        int finalMaxZ = Math.min(maxZ, borderMaxZ);

        Random random = new Random();
        int centerX = (finalMinX + finalMaxX) / 2;
        int centerZ = (finalMinZ + finalMaxZ) / 2;

        int maxAttempts = 100;
        int maxRadius = Math.max(finalMaxX - finalMinX, finalMaxZ - finalMinZ) / 2;

        for (int attempts = 0; attempts < maxAttempts; attempts++) {

            MessageUtils.sendTitleMessage(player
                    , RequisiteRTP.getInstance().getMainConfig().getTeleportLookingForASafeLocationTitle()
                    , RequisiteRTP.getInstance().getMainConfig().getTeleportLookingForASafeLocationSubtitle());


            double angle = Math.toRadians(random.nextInt(360));
            int radius = random.nextInt(maxRadius);

            int x = centerX + (int) (Math.cos(angle) * radius);
            int z = centerZ + (int) (Math.sin(angle) * radius);

            // Ensure it's within allowed boundaries
            if (x < finalMinX || x > finalMaxX || z < finalMinZ || z > finalMaxZ) {
                continue;
            }

            int y = world.getHighestBlockYAt(x, z) + 1;
            Location randomLocation = new Location(world, x + 0.5, y, z + 0.5);

            MessageUtils.toConsole("Trying location " + randomLocation, true);
            if (isSafeLocation(randomLocation) && isLocationAllowed(player, randomLocation)) {
                MessageUtils.toConsole("Found a valid location " + randomLocation, true);
                return randomLocation;
            }
        }

        MessageUtils.toConsole("Failed to find a safe location after " + maxAttempts + " attempts.", true);
        return null;
    }


    private boolean isSafeLocation(Location location) {
        if (location == null) return false;

        Block block = location.getBlock();
        Block above = block.getRelative(BlockFace.UP);
        Block below = block.getRelative(BlockFace.DOWN);

        Material belowType = below.getType();
        Material blockType = block.getType();
        Material aboveType = above.getType();

        // Ensure the block below is solid and not dangerous
        if (!belowType.isSolid() || belowType.name().contains("LEAVES") || isDangerousBlock(belowType)) {
            return false;
        }

        // Ensure the block and the one above it are air or passable
        if (isPassable(blockType) || isPassable(aboveType)) {
            return false;
        }
        // Ensure it's not inside a cave or too dark
        return !isDarkCave(location);
    }
    private boolean isDangerousBlock(Material type) {
        return type == Material.LAVA || type == Material.FIRE || type == Material.CACTUS
                || type == Material.MAGMA_BLOCK || type == Material.CAMPFIRE;
    }

    private boolean isPassable(Material type) {
        return type != Material.AIR && type != Material.CAVE_AIR && type != Material.LEGACY_GRASS
                && type != Material.TALL_GRASS && type != Material.FERN
                && type != Material.SNOW && !Tag.DOORS.isTagged(type) && !Tag.FENCES.isTagged(type);
    }

    private boolean isDarkCave(Location location) {
        Block block = location.getBlock();
        return block.getLightLevel() < 4 && block.getRelative(BlockFace.UP, 3).getType().isSolid();
    }


    private boolean isLocationAllowed(Player player,Location location) {
        for (Hook hook : protectionHooks) {
            if (!hook.getHookData().isEnabled()) continue;

            if (Handlers.hasPermission(player, hook.getHookData().getBypassPermission())) return true;
            if (!hook.isAllowed(player,location)) return false;
        }
        return true;
    }
}