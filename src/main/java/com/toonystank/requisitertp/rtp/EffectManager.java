package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.FileConfig;
import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class EffectManager extends FileConfig {

    private static final Map<String, BaseEffect.Effect> effects = new HashMap<>();
    private static final Map<BaseEffect.Effect, BaseEffect> effectInstances = new HashMap<>();

    @Getter
    private static EffectManager effectManager;

    public EffectManager() throws IOException {
        super("effects.yml", false, false);
        effectManager = this;
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
        MessageUtils.toConsole("loading effect "+ effect.getName() ,true);
        loadEffectClassData(effect);
        effects.put(effect.getName(), effect);
        return effect;
    }
    private void loadEffectClassData(BaseEffect.Effect effect) throws IOException {
        boolean enabled = this.getBoolean(effect.getName() + ".enabled", effect.isEnabled());
        effect.setEnabled(enabled);

        List<String> description = this.getStringList(effect.getName() + ".description",effect.getDescription());
        effect.setDescription(description);

        List<String> commandsToRun = this.getStringList(effect.getName() + ".commandsToRun",effect.getCommandsToRun());
        effect.setCommandsToRun(commandsToRun);

        MessageUtils.toConsole("loaded data from config " + enabled + " " + description + " " + commandsToRun,true);
        loadEffectClass(effect);
    }

    private void loadEffectClass(BaseEffect.Effect effect) {
        try {
            Constructor<? extends BaseEffect> constructor = effect.getEffectClass().getConstructor(BaseEffect.Effect.class);
            BaseEffect baseEffect = constructor.newInstance(effect);
            MessageUtils.toConsole("effect class " + baseEffect.getClass().getName() + " is loaded",true);
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

    public Queue<BaseEffect.Effect> getEnabledEffects() {
        Queue<BaseEffect.Effect> enabledEffects = new ConcurrentLinkedQueue<>();
        for (BaseEffect.Effect effect : effects.values()) {
            if (effect.isEnabled()) {
                enabledEffects.add(effect);
            }
        }
        return enabledEffects;
    }


    public CompletionStage<Boolean> runSuitableEffect(Player player) {
        Queue<BaseEffect.Effect> enabledEffects = getEnabledEffects();
        if (enabledEffects.isEmpty()) {
            return CompletableFuture.completedFuture(true);
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        int waitTime = RequisiteRTP.getInstance().getMainConfig().getTeleportWaitingTime() * 20; // Convert to ticks


        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter >= waitTime) {
                    this.cancel();
                    future.complete(true);
                    return;
                }
                for (BaseEffect.Effect enabledEffect : enabledEffects) {
                    BaseEffect baseEffect = effectInstances.get(enabledEffect);
                    if (baseEffect == null) continue;
                    if (baseEffect.hasToStop(player)) {
                        this.cancel();
                        future.complete(false);
                        return;
                    }
                    Bukkit.getScheduler().runTask(RequisiteRTP.getInstance(),() -> effectInstances.get(enabledEffect).applyEffect(player,counter));
                }
                counter += 2;
            }
        }.runTaskTimerAsynchronously(RequisiteRTP.getInstance(), 0L, 2L);

        return future;
    }

}
