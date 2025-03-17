package com.toonystank.requisitertp.rtp;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.rtp.effect.SpiralEffect;
import com.toonystank.requisitertp.utils.FileConfig;
import com.toonystank.requisitertp.utils.Handlers;
import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public class EffectManager extends FileConfig {

    private static final Map<String, BaseEffect.Effect> effects = new HashMap<>();
    private static final Map<BaseEffect.Effect, BaseEffect> effectInstances = new HashMap<>();

    private static EffectManager effectManager;

    public EffectManager() throws IOException {
        super("effects.yml", "data", false, false);
        effectManager = this;
        init();
    }

    public void init() throws IOException {
        registerEffect("Spiral",true, Collections.singletonList("Spawns particles around player"),Collections.emptyList(), "effect.spiral", SpiralEffect.class);
    }

    public static BaseEffect.Effect registerEffect(String name,
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

    public static BaseEffect.Effect registerEffect(BaseEffect.Effect effect) throws IOException {
        MessageUtils.toConsole("loading effect "+ effect.getName() ,true);
        loadEffectClassData(effect);
        effects.put(effect.getName(), effect);
        return effect;
    }
    private static void loadEffectClassData(BaseEffect.Effect effect) throws IOException {
        boolean enabled = effectManager.getBoolean(effect.getName() + ".enabled", effect.isEnabled());
        effect.setEnabled(enabled);

        List<String> description = effectManager.getStringList(effect.getName() + ".description",effect.getDescription());
        effect.setDescription(description);

        List<String> commandsToRun = effectManager.getStringList(effect.getName() + ".commandsToRun",effect.getCommandsToRun());
        effect.setCommandsToRun(commandsToRun);

        MessageUtils.toConsole("loaded data from config " + enabled + " " + description + " " + commandsToRun,true);
        loadEffectClass(effect);
    }

    private static void loadEffectClass(BaseEffect.Effect effect) {
        try {
            Constructor<? extends BaseEffect> constructor = effect.getEffectClass().getConstructor(boolean.class, List.class, List.class);
            BaseEffect baseEffect = constructor.newInstance(effect.isEnabled(), effect.getDescription(), effect.getCommandsToRun());
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

    public List<BaseEffect.Effect> getEnabledEffects() {
        List<BaseEffect.Effect> enabledEffects = new ArrayList<>();
        for (BaseEffect.Effect effect : effects.values()) {
            if (effect.isEnabled()) {
                enabledEffects.add(effect);
            }
        }
        return enabledEffects;
    }

    public void runSuitableEffect(Player player) {
        List<BaseEffect.Effect> suitableEffects = getSuitableEffectsForPlayer(player);
        if (suitableEffects.isEmpty()) return;
        suitableEffects.forEach(effect -> startEffectForPlayer(player, effect));
    }

    private List<BaseEffect.Effect> getSuitableEffectsForPlayer(Player player) {
        List<BaseEffect.Effect> suitableEffects = new ArrayList<>();
        for (BaseEffect.Effect effect : getEnabledEffects()) {
            if (Handlers.hasPermission(player, effect.getPermissionNode())) {
                suitableEffects.add(effect);
            }
        }
        return suitableEffects;
    }

    private void startEffectForPlayer(Player player, BaseEffect.Effect effect) {
        BaseEffect effectInstance = effectInstances.get(effect);
        if (effectInstance == null || BaseEffect.isQueuedTeleportingPlayer(player)) {
            return;
        }
        BaseEffect.addQueuedTeleportingPlayer(player);
        runEffectTask(player, effectInstance);
    }

    private void runEffectTask(Player player, BaseEffect effectInstance) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!BaseEffect.isQueuedTeleportingPlayer(player)) {
                    cancel();
                    return;
                }
                try {
                    effectInstance.runEffect(player);
                    if (++ticks >= 10) {
                        cancel();
                        BaseEffect.removeQueuedTeleportingPlayer(player);
                    }
                } catch (Exception e) {
                    cancel();
                    BaseEffect.removeQueuedTeleportingPlayer(player);
                }
            }
        }.runTaskTimerAsynchronously(RequisiteRTP.getInstance(), 0, 1);
    }
}
