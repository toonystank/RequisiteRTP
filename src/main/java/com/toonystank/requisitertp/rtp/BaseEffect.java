package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class BaseEffect {

    @Getter
    private static final Map<Player,List<Effect>> activeTasks = new ConcurrentHashMap<>();

    private final Effect effect;

    public BaseEffect(Effect effect) {
        this.effect = effect;
    }

    public abstract void applyEffect(Player player, int tickCount);
    public abstract boolean hasToStop(Player player);


    public void setToFile(String path,Object object) {
        try {
            EffectManager.getEffectManager().set(path,object);
        } catch (IOException e) {
            MessageUtils.error("Failed to set value to file " + EffectManager.getEffectManager().fileName + " at path " + path + " with value " + object);
            e.printStackTrace();
        }
    }

    @AllArgsConstructor @NoArgsConstructor @Setter @Getter
    public static class Effect {

        private String name;
        private boolean enabled;
        private List<String> description;
        @Nullable
        private List<String> commandsToRun;
        private String permissionNode;
        @NotNull
        private Class<? extends BaseEffect> effectClass;
    }
}
