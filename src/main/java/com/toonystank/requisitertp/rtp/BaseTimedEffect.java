package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class BaseTimedEffect extends BaseEffect {

    private final Type timeType;

    public BaseTimedEffect(Effect effect, Type type) {
        super(effect);
        this.timeType = type;
    }

    public abstract void applyTimedEffect(Player player, int tickCount);
    public abstract boolean hasToStopTimed(Player player);

    @Override
    public void applyEffect(Player player, int tickCount) {
        if (hasToStop(player)) {
            return;
        }

        int interval;
        switch (timeType) {
            case SECONDS:
                interval = 20;
                break;
            case MINUTES:
                interval = 1200;
                break;
            default:
                interval = 2;
                break;
        }
        if (tickCount % interval == 0) {
            MessageUtils.toConsole("Applying timed effect to player " + player.getName(), true);
            applyTimedEffect(player, tickCount);
        }
    }


    @Override
    public boolean hasToStop(Player player) {
        return hasToStopTimed(player);
    }

    public enum Type {
        TICKS,
        SECONDS,
        MINUTES,
    }
}