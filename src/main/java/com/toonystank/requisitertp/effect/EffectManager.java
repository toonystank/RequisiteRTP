package com.toonystank.requisitertp.effect;

import com.toonystank.effect.BaseEffect;
import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.FileConfig;
import com.toonystank.requisitertp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EffectManager extends FileConfig implements com.toonystank.effect.EffectManager {

    private static final Map<String, BaseEffect.Effect> effects = new HashMap<>();
    private static final Map<BaseEffect.Effect, BaseEffect> effectInstances = new HashMap<>();
    private static final Map<UUID, Set<BaseEffect>> activePlayerEffects = new ConcurrentHashMap<>();
    private static EffectManager instance;

    public EffectManager() throws IOException {
        super("effects.yml", false, false);
        instance = this;
    }

    public static EffectManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EffectManager instance not initialized. Ensure RequisiteRTP plugin is enabled.");
        }
        return instance;
    }

    public BaseEffect.Effect registerEffect(String name,
                                            boolean enabled,
                                            List<String> description,
                                            List<String> commandsToRun,
                                            String permissionNode,
                                            Class<? extends BaseEffect> effectClass) throws IOException {
        BaseEffect.Effect effect = new BaseEffect.Effect();
        effect.setName(name);
        effect.setEnabled(enabled);
        effect.setDescription(description);
        effect.setPermissionNode(permissionNode);
        effect.setCommandsToRun(commandsToRun);
        effect.setEffectClass(effectClass);
        return registerEffect(effect);
    }

    public BaseEffect.Effect registerEffect(BaseEffect.Effect effect) throws IOException {
        MessageUtils.toConsole("loading effect " + effect.getName(), true);
        loadEffectClassData(effect);
        effects.put(effect.getName(), effect);
        return effect;
    }

    private void loadEffectClassData(BaseEffect.Effect effect) throws IOException {
        boolean enabled = this.getBoolean(effect.getName() + ".enabled", effect.isEnabled());
        effect.setEnabled(enabled);

        List<String> description = this.getStringList(effect.getName() + ".description", effect.getDescription());
        effect.setDescription(description);

        List<String> commandsToRun = this.getStringList(effect.getName() + ".commandsToRun", effect.getCommandsToRun());
        effect.setCommandsToRun(commandsToRun);

        MessageUtils.toConsole("loaded data from config " + enabled + " " + description + " " + commandsToRun, true);
        loadEffectClass(effect);
    }

    private void loadEffectClass(BaseEffect.Effect effect) {
        try {
            Constructor<? extends BaseEffect> constructor = effect.getEffectClass().getConstructor(BaseEffect.Effect.class);
            BaseEffect baseEffect = constructor.newInstance(effect);
            MessageUtils.toConsole("effect class " + baseEffect.getClass().getName() + " is loaded", true);
            effectInstances.put(effect, baseEffect);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            MessageUtils.error("Failed to load effect class: " + effect.getName());
        }
    }

    public BaseEffect.Effect getEffect(String name) {
        return effects.get(name);
    }

    public List<BaseEffect.Effect> getEffects() {
        return new ArrayList<>(effects.values());
    }

    @Override
    public List<BaseEffect.Effect> getEnabledEffects() {
        return new ArrayList<>(getQueuedEnabledEffects());
    }

    public Queue<BaseEffect.Effect> getQueuedEnabledEffects() {
        Queue<BaseEffect.Effect> enabledEffects = new ConcurrentLinkedQueue<>();
        for (BaseEffect.Effect effect : effects.values()) {
            if (effect.isEnabled()) {
                enabledEffects.add(effect);
            }
        }
        return enabledEffects;
    }

    @Override
    public boolean unregisterEffect(String name) throws IOException {
        BaseEffect.Effect effect = effects.remove(name);
        if (effect != null) {
            effectInstances.remove(effect);
            set(name, null);
            return true;
        }
        return false;
    }

    public CompletionStage<Boolean> runSuitableEffect(Player player) {
        Queue<BaseEffect.Effect> enabledEffects = getQueuedEnabledEffects();
        if (enabledEffects.isEmpty()) {
            return CompletableFuture.completedFuture(true);
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        int waitTime = RequisiteRTP.getInstance().getMainConfig().getTeleportWaitingTime() * 20;
        BaseEffect.RunningResult runningResult = new BaseEffect.RunningResult(false, false);
        BaseEffect.getActiveEffects().put(player, runningResult);

        Set<BaseEffect> playerEffects = ConcurrentHashMap.newKeySet();
        for (BaseEffect.Effect effect : enabledEffects) {
            BaseEffect baseEffect = effectInstances.get(effect);
            if (baseEffect != null) {
                playerEffects.add(baseEffect);
            }
        }
        activePlayerEffects.put(player.getUniqueId(), playerEffects);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter >= waitTime) {
                    this.cancel();
                    runningResult.setFinished(true);
                    future.complete(true);
                    return;
                }
                boolean firstCall = counter == 0;

                for (BaseEffect.Effect enabledEffect : enabledEffects) {
                    BaseEffect baseEffect = effectInstances.get(enabledEffect);
                    if (baseEffect == null) continue;
                    if (baseEffect.hasToStop(player, firstCall)) {
                        runningResult.setCancelled(true);
                        this.cancel();
                        future.complete(false);
                        break;
                    }
                    Bukkit.getScheduler().runTask(RequisiteRTP.getInstance(), () -> {
                        if (BaseEffect.isCancelled(player)) {
                            MessageUtils.toConsole("Effect is canceled", true);
                            return;
                        }
                        effectInstances.get(enabledEffect).applyEffect(player, counter);
                    });
                }
                counter += 2;
            }
        }.runTaskTimerAsynchronously(RequisiteRTP.getInstance(), 0L, 2L);

        return future;
    }

    public void notifyTeleportComplete(Player player) {
        UUID playerUUID = player.getUniqueId();
        Set<BaseEffect> playerEffects = activePlayerEffects.getOrDefault(playerUUID, Collections.emptySet());
        for (BaseEffect effect : playerEffects) {
            effect.onTeleportComplete(player);
        }
        activePlayerEffects.remove(playerUUID);
        MessageUtils.debug("Notified teleport completion for player: " + player.getName());
    }
}