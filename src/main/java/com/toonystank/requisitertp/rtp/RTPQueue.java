package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.Handlers;
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

    public boolean addPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (forTeleportQueue.contains(playerUUID)) return false;

        return forTeleportQueue.add(playerUUID);
    }

    public boolean isInQueue(Player player) {
        return forTeleportQueue.contains(player.getUniqueId());
    }

    private void processQueue() {
        Handlers.runTaskTimerAsync(20,() -> {
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

            Handlers.runTask(() ->randomLocation.getChunk().load());

            rtpManager.getEffectManager().runSuitableEffect(player).whenComplete((result, ignored) -> {
                onTeleportQueue.poll();
                if (result) {
                    // removes the player from teleport queue as the player is already teleported. this is here to prevent duplicated teleports.

                    Handlers.runTask(() -> PaperLib.teleportAsync(player, randomLocation).whenComplete((successfullyTeleported, throwable) -> {
                        if (successfullyTeleported) {
                            rtpManager.getEffectManager().notifyTeleportComplete(player);
                            MessageUtils.sendTitleMessage(player
                                    ,RequisiteRTP.getInstance().getMainConfig().getTeleportSuccessfullyTeleported()
                                    ,RequisiteRTP.getInstance().getMainConfig().getTeleportSuccessfullyTeleportedSubTitle());
                        }
                    }));
                }else {
                    MessageUtils.sendMessage(player,"&cAn error occurred while teleporting you to a random location. Please try again.");
                }
            });

        });
    }
}
