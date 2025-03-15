package com.toonystank.requisitertp.teleport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class TeleportEffect {

    @Getter
    private static final List<Player> queuedTeleportingPlayers = new ArrayList<>();

    private final Effect effect;

    public TeleportEffect(String name
            , boolean enabled
            , List<String> description
            , List<String> commandsToRun
            , Class<? extends TeleportEffect> effectClass) {
        this.effect = new Effect(name, enabled, description, commandsToRun,effectClass);
        TeleportManager.registerEffect(effect);
    }

    public abstract void runEffect(Player player);

    public void addQueuedTeleportingPlayer(Player player) {
        queuedTeleportingPlayers.add(player);
    }
    public void removeQueuedTeleportingPlayer(Player player) {
        queuedTeleportingPlayers.remove(player);
    }
    public boolean isQueuedTeleportingPlayer(Player player) {
        return queuedTeleportingPlayers.contains(player);
    }
    @AllArgsConstructor @NoArgsConstructor @Setter @Getter
    public static class Effect {

        private String name;
        private boolean enabled;
        private List<String> description;
        @Nullable
        private List<String> commandsToRun;
        @NotNull
        private Class<? extends TeleportEffect> effectClass;
    }
}
