package com.toonystank.requisitertp.effect.implementations;

import com.toonystank.effect.BaseTimedEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PotionEffect extends BaseTimedEffect {

    public PotionEffect(Effect effect) {
        // Run every second (20 ticks)
        super(effect, Type.SECONDS);
    }

    @Override
    public void load() {

    }

    @Override
    public void clearData() {

    }

    @Override
    public void applyTimedEffect(Player player, int tickCount) {
        int duration = 40; // 2 seconds (40 ticks)
        int amplifier = 0; // Blindness I
        boolean ambient = false;
        boolean particles = false;

        player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier, ambient, particles));
    }

    @Override
    public boolean hasToStop(Player player, boolean firstCall) {
        return false;
    }

    @Override
    public void onTeleportComplete(Player player) {
    }
}
