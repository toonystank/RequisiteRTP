package com.toonystank.requisitertp;

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
            teleportQueue.add(player.getUniqueId());
        }
    }

    private void processQueue() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(RequisiteRTP.getInstance(), () -> {
            while (!teleportQueue.isEmpty()) {
                UUID playerUUID = teleportQueue.poll();
                if (playerUUID != null) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player != null && player.isOnline()) {
                        World world = player.getWorld();
                        executorService.submit(() -> {
                            Location randomLocation = rtpManager.findSafeLocation(world);
                            if (randomLocation != null) {
                                Bukkit.getScheduler().runTask(RequisiteRTP.getInstance(), () -> player.teleport(randomLocation));
                            }
                        });
                    }
                }
            }
        }, 0L, 20L); // Runs every second
    }
}
