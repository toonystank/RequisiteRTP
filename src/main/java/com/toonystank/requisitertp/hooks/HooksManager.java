package com.toonystank.requisitertp.hooks;

import com.toonystank.requisitertp.hooks.implementations.GriefPreventionHook;
import com.toonystank.requisitertp.utils.FileConfig;
import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class HooksManager {

    @Getter
    private static List<Hook> hooks = new ArrayList<>();

    private final HookConfig hookConfig;

    public HooksManager() {
        try {
            this.hookConfig = new HookConfig();
        } catch (IOException e) {
            MessageUtils.error("An error happened when loading hooks.yml " + e.getMessage());
            throw new RuntimeException(e);
        }
        MessageUtils.toConsole("Loading hooks...",false);
        initialize();
        MessageUtils.toConsole(hooks.size() + " Hooks loaded",false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Hook registerHook(String name,boolean isEnabled,String bypassPermission,Class<? extends Hook> hookClass) {
        isEnabled = hookConfig.isEnabled(name,isEnabled);
        bypassPermission = hookConfig.getBypassPermission(name,bypassPermission);
        if (!isEnabled) {
            return null;
        }

        Hook.HookData hookData = new Hook.HookData(name, true,bypassPermission);

        try {
            Constructor<? extends Hook> constructor = hookClass.getConstructor(Hook.HookData.class);
            Hook flagInstance = constructor.newInstance(hookData);
            hooks.add(flagInstance);
            return flagInstance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            MessageUtils.error("An error happened when loading hook " + name + " " + e.getMessage());
            return null;
        }
    }

    public void initialize() {
        registerHook("GriefPrevention",true,"requisitertp.bypass.griefprevention", GriefPreventionHook.class);

    }


    private static class HookConfig extends FileConfig {
        public HookConfig() throws IOException {
            super("hooks.yml",false,false);
        }

        public boolean isEnabled(String hookName, boolean defaultValue) {
            try {
                return getBoolean(hookName + ".enabled", defaultValue);
            }catch (Exception e) {
                return defaultValue;
            }
        }
        public boolean isEnabled(String hookName) {
            try {
                return getBoolean(hookName + ".enabled");
            }catch (Exception e) {
                return false;
            }
        }
        public String getBypassPermission(String hookName, String defaultValue) {
            try {
                return getString(hookName + ".bypassPermission", defaultValue);
            }catch (Exception e) {
                return defaultValue;
            }
        }
        public String getBypassPermission(String hookName) {
            try {
                return getString(hookName + ".bypassPermission");
            }catch (Exception e) {
                return null;
            }
        }
    }

    public void reload() {
        try {
            hookConfig.reload();
        } catch (IOException e) {
            MessageUtils.error("An error happened when reloading hooks.yml " + e.getMessage());
            return;
        }

        for (Hook hook : hooks) {
            Hook.HookData currentHookData = hook.getHookData();
            boolean isEnabled = hookConfig.isEnabled(currentHookData.getName());
            String bypassPermission = hookConfig.getBypassPermission(currentHookData.getName());
            if (!isEnabled) {
                return;
            }
            currentHookData.setEnabled(true);
            currentHookData.setBypassPermission(bypassPermission);
            hook.setHookData(currentHookData);
        }
        MessageUtils.toConsole("Reloaded hooks",false);
    }

}
