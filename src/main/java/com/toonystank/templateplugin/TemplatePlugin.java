package com.toonystank.templateplugin;

import com.toonystank.templateplugin.utils.MainConfig;
import com.toonystank.templateplugin.utils.MessageUtils;

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class TemplatePlugin extends JavaPlugin {

    // Change this for all the console outputs
    private final String pluginName = "TemplatePlugin";

    @Getter
    private static TemplatePlugin instance;

    private BukkitAudiences adventure;
    private MainConfig mainConfig;


    @Override
    public void onEnable() {
        instance = this;

        this.adventure = BukkitAudiences.create(this);
        try {
            this.mainConfig = new MainConfig();
        } catch (IOException e) {
            MessageUtils.error("An error happend when loading config.yml " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        adventure.close();
        // Plugin shutdown logic
    }


    public void reload() {
        MessageUtils.toConsole("reloading plugin....",false);
        long time = System.currentTimeMillis();
        try {
            this.mainConfig.reload();
        } catch (IOException e) {
            MessageUtils.error("An error happend while reloading the plugin " + e.getMessage());
            e.printStackTrace();
        }
        long currentTime = time - System.currentTimeMillis();
        MessageUtils.toConsole("Successfully reloaded the plugin in " + currentTime + " ms",false);

        
    }
}
