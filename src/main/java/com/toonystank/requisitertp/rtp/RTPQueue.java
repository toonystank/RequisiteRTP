package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.RequisiteRTP;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RTPQueue {

    private final Queue<UUID> teleportQueue;
    private final RTPManager rtpManager;
    private final ExecutorService executorService;

    public RTPQueue(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
        this.teleportQueue = new ConcurrentLinkedQueue<>();
        this.executorService = Executors.newFixedThreadPool(2);
        processQueue();
    }

    public void addPlayer(Player player) {
        if (!teleportQueue.contains(player.getUniqueId())) {
            rtpManager.getEffectManager().runSuitableEffect(player);
            teleportQueue.add(player.getUniqueId());
        }
    }

    private void processQueue() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(RequisiteRTP.getInstance(), () -> {
            if (teleportQueue.isEmpty()) {
                return;
            }
            UUID playerUUID = teleportQueue.poll();
            if (playerUUID == null) return;
            Player player = Bukkit.getPlayer(playerUUID);
            if (!(player != null && player.isOnline())) return;
            World world = player.getWorld();
            executorService.submit(() -> {
                Location randomLocation = rtpManager.findSafeLocation(world);
                if (randomLocation == null) return;
                Bukkit.getScheduler().runTask(RequisiteRTP.getInstance(), () -> {
                    randomLocation.getChunk().load();
                    PaperLib.teleportAsync(player, randomLocation).thenRun(() -> BaseEffect.removeQueuedTeleportingPlayer(player));
                });

            });

        }, 0L, 1); // Runs every second
    }
}
