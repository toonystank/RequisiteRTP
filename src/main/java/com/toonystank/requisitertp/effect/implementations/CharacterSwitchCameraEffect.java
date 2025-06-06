package com.toonystank.requisitertp.effect.implementations;

import com.toonystank.effect.BaseTimedEffect;
import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CharacterSwitchCameraEffect extends BaseTimedEffect {

    private static final Random RANDOM = new Random();
    private final Map<UUID, Location> playerLocations = new ConcurrentHashMap<>();
    private final Map<UUID, ArmorStand> cameraAnchors = new ConcurrentHashMap<>();
    private final int totalDurationTicks;

    public CharacterSwitchCameraEffect(Effect effect) {
        super(effect, Type.TICKS);
        this.totalDurationTicks = RequisiteRTP.getInstance().getMainConfig().getTeleportWaitingTime() * 20;
        MessageUtils.debug("CharacterSwitchCameraEffect initialized with duration: " + totalDurationTicks + " ticks.");
    }

    @Override
    public void load() {
        // No-op
    }

    @Override
    public void clearData() {
        // No-op
    }

    @Override
    public void applyTimedEffect(Player player, int tickCount) {
        UUID playerUUID = player.getUniqueId();
        Location currentLoc = player.getLocation();

        if (tickCount == 2) {
            playerLocations.put(playerUUID, currentLoc.clone());
            setupCamera(player, currentLoc);
            player.playSound(currentLoc, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.8f, 1.0f);
            MessageUtils.debug("Started camera animation for player: " + player.getName());
        }

        if (hasToStop(player, tickCount == 2)) {
            return;
        }

        ArmorStand camera = cameraAnchors.get(playerUUID);
        if (camera == null) {
            MessageUtils.debug("Camera anchor not found for player: " + player.getName());
            return;
        }

        double progress = (double) (tickCount - 2) / (totalDurationTicks - 2);
        Location newCameraLoc = calculateCameraPosition(currentLoc, tickCount, progress);
        camera.teleport(newCameraLoc);

        if (progress >= 0.2) {
            World world = newCameraLoc.getWorld();
            world.spawnParticle(Particle.SMOKE_NORMAL, newCameraLoc, 5, 0.2, 0.2, 0.2, 0.02);
        }
    }

    private void setupCamera(Player player, Location playerLoc) {
        World world = playerLoc.getWorld();
        Location cameraLoc = playerLoc.clone().add(0, 2, 0);

        ArmorStand cameraAnchor = world.spawn(cameraLoc, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setMarker(true);
            stand.setSilent(true);
            stand.setInvulnerable(true);
            stand.setCollidable(false);
            stand.setAI(false);
            stand.setCustomNameVisible(false);
        });

        cameraAnchors.put(player.getUniqueId(), cameraAnchor);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(cameraAnchor);
                MessageUtils.debug("Player is now spectating camera anchor at: " + cameraLoc);
            }
        }.runTaskLater(RequisiteRTP.getInstance(), 1L);
    }

    private Location calculateCameraPosition(Location target, int tickCount, double progress) {
        Location cameraLoc = target.clone();
        double maxHeight = 60.0;
        double shakeAmplitude = 0.5;

        if (progress < 0.2) {
            double height = maxHeight * (progress / 0.2);
            cameraLoc.add(0, height, 0);
        } else {
            cameraLoc.add(0, maxHeight, 0);
            double shakeX = shakeAmplitude * Math.sin(tickCount * 0.1 + RANDOM.nextDouble());
            double shakeZ = shakeAmplitude * Math.cos(tickCount * 0.15 + RANDOM.nextDouble());
            cameraLoc.add(shakeX, 0, shakeZ);
        }

        cameraLoc.setPitch(90);
        return cameraLoc;
    }

    @Override
    public void onTeleportComplete(Player player) {
        UUID playerUUID = player.getUniqueId();
        ArmorStand camera = cameraAnchors.get(playerUUID);

        if (camera == null) {
            resetCamera(player, playerUUID); // Fallback
            return;
        }

        Location targetLocation = player.getLocation().clone().add(0, 2, 0);
        Location startLocation = camera.getLocation();

        new BukkitRunnable() {
            double t = 0;
            final int steps = 20;
            final Location start = startLocation.clone();
            final Location end = targetLocation.clone();

            @Override
            public void run() {
                if (t >= 1.0) {
                    resetCamera(player, playerUUID);
                    cancel();
                    return;
                }

                double progress = t;
                double newY = start.getY() + (end.getY() - start.getY()) * progress;
                Location intermediate = new Location(
                        start.getWorld(),
                        start.getX(),
                        newY,
                        start.getZ(),
                        0,
                        90
                );

                ArmorStand camera = cameraAnchors.get(playerUUID);
                if (camera != null) {
                    camera.teleport(intermediate);
                }

                t += 1.0 / steps;
            }
        }.runTaskTimer(RequisiteRTP.getInstance(), 0L, 1L);
    }

    private void resetCamera(Player player, UUID playerUUID) {
        ArmorStand camera = cameraAnchors.remove(playerUUID);
        playerLocations.remove(playerUUID);

        if (camera != null) {
            camera.remove();
            MessageUtils.debug("Camera anchor removed for player: " + player.getName());
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(player.getLocation());

        MessageUtils.debug("Camera reset to player: " + player.getName());

        Location playerLoc = player.getLocation();
        playerLoc.getWorld().spawnParticle(Particle.PORTAL, playerLoc, 30, 0.5, 0.5, 0.5, 0.1);
        player.playSound(playerLoc, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
    }

    @Override
    public boolean hasToStop(Player player, boolean firstCall) {
        return false;
    }

    private boolean isLocationEqual(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null || loc1.getWorld() != loc2.getWorld()) {
            return false;
        }
        return Math.abs(loc1.getX() - loc2.getX()) < 0.1 &&
                Math.abs(loc1.getY() - loc2.getY()) < 0.1 &&
                Math.abs(loc1.getZ() - loc2.getZ()) < 0.1;
    }
}
