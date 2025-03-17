package com.toonystank.requisitertp.rtp.effect;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.rtp.BaseEffect;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SpiralEffect extends BaseEffect {

    public SpiralEffect(boolean enabled, List<String> description, List<String> commandsToRun) {
        super("Spiral", enabled, description, commandsToRun, "effect.spiral", SpiralEffect.class);
    }
    public SpiralEffect(BaseEffect.Effect effect) {
        super(effect);
    }

    @Override
    public void runEffect(Player player) {
        if (!player.isOnline() || isQueuedTeleportingPlayer(player)) return;

        new BukkitRunnable() {
            double t = 0;
            final Location location = player.getLocation();

            @Override
            public void run() {
                if (!player.isOnline() || !isQueuedTeleportingPlayer(player)) {
                    removeQueuedTeleportingPlayer(player);
                    cancel();
                    return;
                }

                double x = 0.5 * t * Math.cos(t);
                double y = 0.1 * t;
                double z = 0.5 * t * Math.sin(t);
                location.getWorld().spigot().playEffect(location.clone().add(x, y, z), org.bukkit.Effect.FLAME, 0, 0, 0, 0, 0, 1, 1, 16);
                t += Math.PI / 8;

                if (t > Math.PI * 4) {
                    removeQueuedTeleportingPlayer(player);
                    cancel();
                }
            }
        }.runTask(RequisiteRTP.getInstance());
    }
}
