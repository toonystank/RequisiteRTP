package com.toonystank.requisitertp.rtp;

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
public abstract class BaseEffect {

    @Getter
    private static final List<Player> queuedTeleportingPlayers = new ArrayList<>();

    private final Effect effect;

    public BaseEffect(String name
            , boolean enabled
            , List<String> description
            , List<String> commandsToRun
            , String permissionNode
            , Class<? extends BaseEffect> effectClass) {
        this.effect = new Effect(name, enabled, description, commandsToRun,permissionNode,effectClass);
    }
    public BaseEffect(Effect effect) {
        this.effect = effect;
    }

    public abstract void runEffect(Player player);

    public static void addQueuedTeleportingPlayer(Player player) {
        queuedTeleportingPlayers.add(player);
    }
    public static void removeQueuedTeleportingPlayer(Player player) {
        queuedTeleportingPlayers.remove(player);
    }
    public static boolean isQueuedTeleportingPlayer(Player player) {
        return queuedTeleportingPlayers.contains(player);
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
