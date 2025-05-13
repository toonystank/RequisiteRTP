package com.toonystank.requisitertp.effect;

import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class BaseTimedEffect extends BaseEffect {

    private final Type timeType;
    private final int interval;

    public BaseTimedEffect(Effect effect, Type type) {
        super(effect);
        this.timeType = type;
        this.interval = getInterval(type);
    }

    private static int getInterval(Type type) {
        switch (type) {
            case SECONDS: return 20;
            case MINUTES: return 1200;
            default: return 2;
        }
    }

    public abstract void applyTimedEffect(Player player, int tickCount);

    @Override
    public void applyEffect(Player player, int tickCount) {
        if (!hasToStop(player, false) && tickCount % interval == 0) {
            MessageUtils.toConsole("Applying timed effect to " + player.getName(), true);
            applyTimedEffect(player, tickCount);
        }
    }

    public enum Type {
        TICKS,
        SECONDS,
        MINUTES
    }
}
