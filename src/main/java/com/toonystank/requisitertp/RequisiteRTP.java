package com.toonystank.requisitertp;

import com.toonystank.requisitertp.command.RTPCommand;
import com.toonystank.requisitertp.rtp.RTPManager;
import com.toonystank.requisitertp.utils.MainConfig;
import com.toonystank.requisitertp.utils.MessageUtils;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RequisiteRTP extends JavaPlugin {

    // Change this for all the console outputs
    private final String pluginName = "RequisiteRTP";

    @Getter
    private static RequisiteRTP instance;

    private BukkitAudiences adventure;
    private MainConfig mainConfig;
    private RTPCommand rtpCommand;
    private RTPManager rtpManager;



    @Override
    public void onEnable() {
        instance = this;

        this.adventure = BukkitAudiences.create(this);
        MessageUtils.setAudience(adventure);

        try {
            this.mainConfig = new MainConfig();
        } catch (IOException e) {
          MessageUtils.error("An error happened when loading config.yml " + e.getMessage());
            e.printStackTrace();
        }
        this.rtpCommand = new RTPCommand(this);
        this.rtpManager = new RTPManager();

    }

    @Override
    public void onDisable() {
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
