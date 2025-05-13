package com.toonystank.requisitertp.effect.implementations;

import com.toonystank.requisitertp.effect.BaseTimedEffect;
import org.bukkit.entity.Player;

public class SoundEffect extends BaseTimedEffect {



    public SoundEffect(Effect effect) {
        super(effect, Type.SECONDS);
    }

    @Override
    public void applyTimedEffect(Player player, int tickCount) {

    }

    @Override
    public boolean hasToStop(Player player, boolean firstCall) {
        return false;
    }
}
