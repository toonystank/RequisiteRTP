package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.MessageUtils;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RTPQueue {

    private final Queue<UUID> teleportQueue;
    private final RTPManager rtpManager;

    public RTPQueue(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
        this.teleportQueue = new ConcurrentLinkedQueue<>();
        processQueue();
    }

    public void addPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (teleportQueue.contains(playerUUID)) return;

        teleportQueue.add(playerUUID);
    }

    private void processQueue() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(RequisiteRTP.getInstance(), () -> {
            if (teleportQueue.isEmpty()) return;

            UUID playerUUID = teleportQueue.peek();
            if (playerUUID == null) return;

            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null || !player.isOnline()) return;
            World world = player.getWorld();

            Location randomLocation = rtpManager.findSafeLocation(player, world);
            if (randomLocation == null) return;

            rtpManager.getEffectManager().runSuitableEffect(player).whenComplete((result, ignored) -> {
                if (result) {
                    teleportQueue.remove(playerUUID);

                    // Load chunk & teleport
                    Bukkit.getScheduler().runTask(RequisiteRTP.getInstance(), () -> {
                        randomLocation.getChunk().load();
                        PaperLib.teleportAsync(player, randomLocation);
                    });
                }else {
                    MessageUtils.sendMessage(player,"&cAn error occurred while teleporting you to a random location. Please try again.");
                }
            });

        }, 0L, 20L);
    }
}
