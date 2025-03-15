package com.toonystank.requisitertp.teleport;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TeleportManager {

    private final static Map<String, TeleportEffect.Effect> effects = new HashMap<>();

    public static TeleportEffect.Effect registerEffect(String name
            , boolean enabled
            , List<String> description
            , List<String> commandsToRun
            , Class<? extends TeleportEffect> effectClass) {
        TeleportEffect.Effect effect = new TeleportEffect.Effect();
        effect.setName(name);
        effect.setEnabled(enabled);
        effect.setDescription(description);
        effect.setCommandsToRun(commandsToRun);
        effect.setEffectClass(effectClass);
        return registerEffect(effect);
    }
    public static TeleportEffect.Effect registerEffect(TeleportEffect.Effect effect) {
        effects.put(effect.getName(), effect);
        return effect;
    }

    public TeleportEffect.Effect getEffect(String name) {
        return effects.get(name);
    }
    public List<TeleportEffect.Effect> getEffects() {
        return new ArrayList<>(effects.values());
    }

    public List<TeleportEffect.Effect> getEnabledEffects() {
        List<TeleportEffect.Effect> enabledEffects = new ArrayList<>();
        for (TeleportEffect.Effect effect : effects.values()) {
            if (effect.isEnabled()) {
                enabledEffects.add(effect);
            }
        }
        return enabledEffects;
    }




}
