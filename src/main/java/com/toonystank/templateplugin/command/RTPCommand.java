package com.toonystank.templateplugin.command;

import com.toonystank.templateplugin.TemplatePlugin;
import com.toonystank.templateplugin.manager.BaseCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RTPCommand extends BaseCommand{

    protected RTPCommand(TemplatePlugin plugin) {
        super(plugin, "rtp"
                ,false
                , false
                ,"Teleport to a random location"
                ,"/rtp"
                ,"rtp"
                , "wild", "wilderness", "randomtp");
        registerSubCommand("reload", new ReloadCommand());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void execute(ConsoleCommandSender sender, String[] args) {
        if (!(args.length > 0)) return;

        Player player = TemplatePlugin.getInstance().getServer().getPlayer(args[0]);
        if (player == null) return;

    
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length > 0) {
            player = TemplatePlugin.getInstance().getServer().getPlayer(args[0]);
        }
        if (player == null) return;
        
    }
}
