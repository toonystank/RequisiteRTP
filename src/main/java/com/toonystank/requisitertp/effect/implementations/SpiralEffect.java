package com.toonystank.requisitertp.effect.implementations;

import com.toonystank.effect.BaseEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class SpiralEffect extends BaseEffect {


    public SpiralEffect(BaseEffect.Effect effect) {
        super(effect);
    }

    @Override
    public void load() {

    }

    @Override
    public void clearData() {

    }

    @Override
    public void applyEffect(Player player, int tickCount) {
        Location loc = player.getLocation();
        double radius = 1.5 + (tickCount * 0.01);
        double maxYOffset = 3.0;
        double yOffset = (tickCount * 0.1) % (maxYOffset * 2);
        if (yOffset > maxYOffset) {
            yOffset = maxYOffset - (yOffset - maxYOffset);
        }
        int spirals = 3;
        double speed = 0.2;
        Particle[] particles = {Particle.FLAME, Particle.CLOUD, Particle.CRIT};

        for (int j = 0; j < spirals; j++) {
            for (int i = 0; i < 360; i += 20) {
                double angle = Math.toRadians(i + (tickCount * speed * j * 10));
                double x = loc.getX() + (radius * Math.cos(angle));
                double z = loc.getZ() + (radius * Math.sin(angle));
                double y = loc.getY() + yOffset - (j * 0.2);
                Location particleLoc = new Location(loc.getWorld(), x, y, z);

                Particle particle = particles[(i / 20) % particles.length];
                loc.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }



    @Override
    public boolean hasToStop(Player player, boolean FirstCall) {
        return false;
    }

    @Override
    public void onTeleportComplete(Player player) {
    }
}
