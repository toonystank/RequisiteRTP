package com.toonystank.requisitertp.effect.implementations;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.toonystank.effect.BaseTimedEffect;
import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
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

        Location newCameraLoc = calculateCameraPosition(player, currentLoc, tickCount, progress);
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
                sendCameraPacket(player, cameraAnchor.getEntityId());
                MessageUtils.debug("Camera anchor spawned and packet sent at: " + cameraLoc + " for player: " + player.getName());
            }
        }.runTaskLater(RequisiteRTP.getInstance(), 1L);
    }

    private Location calculateCameraPosition(Player player, Location target, int tickCount, double progress) {
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
        resetCamera(player, playerUUID);
    }

    private void resetCamera(Player player, UUID playerUUID) {
        ArmorStand camera = cameraAnchors.remove(playerUUID);
        playerLocations.remove(playerUUID);

        if (camera != null) {
            camera.remove();
            MessageUtils.debug("Camera anchor removed for player: " + player.getName());
        }

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                com.comphenix.protocol.PacketType.Play.Server.CAMERA);
        packet.getIntegers().write(0, player.getEntityId());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            MessageUtils.debug("Camera reset to player: " + player.getName());
        } catch (Exception e) {
            MessageUtils.error("Failed to reset camera: " + e.getMessage());
        }

        Location playerLoc = player.getLocation();
        playerLoc.getWorld().spawnParticle(Particle.PORTAL, playerLoc, 30, 0.5, 0.5, 0.5, 0.1);
        player.playSound(playerLoc, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
    }

    private void sendCameraPacket(Player player, int entityId) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                com.comphenix.protocol.PacketType.Play.Server.CAMERA);
        packet.getIntegers().write(0, entityId);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            MessageUtils.debug("Camera packet sent to entity ID: " + entityId + " for player: " + player.getName());
        } catch (Exception e) {
            MessageUtils.error("Failed to send camera packet: " + e.getMessage());
        }
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