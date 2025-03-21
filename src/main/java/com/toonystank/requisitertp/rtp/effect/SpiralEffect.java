package com.toonystank.requisitertp.rtp.effect;

import com.toonystank.requisitertp.rtp.BaseEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class SpiralEffect extends BaseEffect {

    public SpiralEffect(BaseEffect.Effect effect) {
        super(effect);
    }

    @Override
    public void applyEffect(Player player, int tickCount) {
        Location loc = player.getLocation();
        double radius = 1.5;
        double yOffset = tickCount * 0.05; // Spiral effect increases over time

        for (int i = 0; i < 360; i += 30) {
            double angle = Math.toRadians(i);
            double x = loc.getX() + radius * Math.cos(angle);
            double z = loc.getZ() + radius * Math.sin(angle);
            Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + yOffset, z);
            loc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean hasToStop(Player player) {

        return false;
    }
}
