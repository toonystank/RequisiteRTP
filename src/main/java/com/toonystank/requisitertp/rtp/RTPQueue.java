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

    private final Queue<UUID> forTeleportQueue;
    private final Queue<UUID> onTeleportQueue;
    private final RTPManager rtpManager;

    public RTPQueue(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
        this.forTeleportQueue = new ConcurrentLinkedQueue<>();
        this.onTeleportQueue = new ConcurrentLinkedQueue<>();
        processQueue();
    }

    public void addPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (forTeleportQueue.contains(playerUUID)) return;

        forTeleportQueue.add(playerUUID);
    }

    private void processQueue() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(RequisiteRTP.getInstance(), () -> {
            if (forTeleportQueue.isEmpty()) return;

            UUID playerUUID = forTeleportQueue.poll();
            if (playerUUID == null) return;

            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null || !player.isOnline()) return;

            if (onTeleportQueue.contains(playerUUID)) {
                MessageUtils.sendMessage(player,RequisiteRTP.getInstance().getMainConfig().getTeleportYouAlreadyOnTeleportQueue());
                return;
            }
            onTeleportQueue.add(playerUUID);


            World world = player.getWorld();

            Location randomLocation = rtpManager.findSafeLocation(player, world);
            if (randomLocation == null) return;

            rtpManager.getEffectManager().runSuitableEffect(player).whenComplete((result, ignored) -> {
                onTeleportQueue.poll();
                if (result) {
                    // removes the player from teleport queue as the player is already teleported. this is here to prevent duplicated teleports.

                    Bukkit.getScheduler().runTask(RequisiteRTP.getInstance(), () -> {
                        randomLocation.getChunk().load();
                        PaperLib.teleportAsync(player, randomLocation).whenComplete((successfullyTeleported,throwable) -> {
                            if (successfullyTeleported) {
                                MessageUtils.sendTitleMessage(player
                                        ,RequisiteRTP.getInstance().getMainConfig().getTeleportSuccessfullyTeleported()
                                        ,RequisiteRTP.getInstance().getMainConfig().getTeleportSuccessfullyTeleportedSubTitle());
                            }
                        });
                    });
                }else {
                    MessageUtils.sendMessage(player,"&cAn error occurred while teleporting you to a random location. Please try again.");
                }
            });

        }, 0L, 20L);
    }
}
