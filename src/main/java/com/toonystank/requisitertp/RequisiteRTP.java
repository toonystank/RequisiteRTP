package com.toonystank.requisitertp;

import com.toonystank.requisitertp.command.RTPCommand;
import com.toonystank.requisitertp.data.WorldManager;
import com.toonystank.requisitertp.hooks.HooksManager;
import com.toonystank.requisitertp.rtp.RTPManager;
import com.toonystank.requisitertp.config.MainConfig;
import com.toonystank.requisitertp.utils.MessageUtils;

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.io.IOException;
import java.util.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RequisiteRTP extends JavaPlugin {

    // Change this for all the console outputs
    private final String pluginName = "RequisiteRTP";

    @Getter
    private static RequisiteRTP instance;

    private BukkitAudiences adventure;
    private MainConfig mainConfig;
    private HooksManager hooksManager;

    private WorldManager worldManager;
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
        this.hooksManager = new HooksManager();

        try {
            this.worldManager = new WorldManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.rtpCommand = new RTPCommand(this);
        this.rtpManager = new RTPManager(worldManager);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public void reload(CommandSender player) {
        MessageUtils.sendMessage(player,"reloading plugin....");
        long time = System.currentTimeMillis();
        try {
            this.mainConfig.reload();
            this.rtpManager.reload();
            this.hooksManager.reload();
            this.worldManager.reload();

        } catch (IOException e) {
           MessageUtils.error("An error happend while reloading the plugin " + e.getMessage());
            e.printStackTrace();
        }
        long currentTime = time - System.currentTimeMillis();
        MessageUtils.sendMessage(player,"Successfully reloaded the plugin in " + currentTime + " ms");
    }
}
