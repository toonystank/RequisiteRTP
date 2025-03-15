package com.toonystank.requisitertp.teleport.effect;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.teleport.TeleportEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import java.util.List;

public class SpiralEffect extends TeleportEffect {

    private static final double RADIUS_INCREMENT = 0.5; // Control the size of the spiral loop
    private static final double ANGLE_INCREMENT = Math.PI / 12; // Control the angle increment for the spiral

    public SpiralEffect(boolean enabled, List<String> description, List<String> commandsToRun) {
        super("Spiral", enabled, description, commandsToRun, SpiralEffect.class);
    }

    @Override
    public void runEffect(Player player) {
        if (!getEffect().isEnabled()) return;
        addQueuedTeleportingPlayer(player);

        // Start the spiral effect on a scheduled task
        new BukkitRunnable() {
            private double angle = 0;
            private double radius = 1; // Starting radius of the spiral

            @Override
            public void run() {
                if (!isQueuedTeleportingPlayer(player)) {
                    cancel(); // Cancel the task if the player is no longer queued for teleportation
                    return;
                }

                // Calculate the next position in the spiral
                double x = player.getLocation().getX() + radius * Math.cos(angle);
                double z = player.getLocation().getZ() + radius * Math.sin(angle);
                double y = player.getLocation().getY(); // You can adjust the y-coordinate if you want the player to change height

                // Teleport the player to the calculated position
                player.teleport(player.getWorld().getBlockAt((int) x, (int) y, (int) z).getLocation());

                // Increment angle and radius for the next step
                angle += ANGLE_INCREMENT;
                radius += RADIUS_INCREMENT;

                // Optionally, stop after a certain number of spirals or time
                if (radius > 10) { // For example, stop after the spiral grows too large
                    cancel();
                }
            }
        }.runTaskTimer(RequisiteRTP.getInstance(), 0, 1); // Run every tick (1/20 second)
    }
}
