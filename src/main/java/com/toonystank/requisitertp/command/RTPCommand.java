package com.toonystank.requisitertp.command;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.manager.BaseCommand;

import com.toonystank.requisitertp.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RTPCommand extends BaseCommand{

    private final RequisiteRTP plugin;

    public RTPCommand(RequisiteRTP plugin) {
        super(plugin, "rtp"
                ,false
                , false
                ,"Teleport to a random location"
                ,"/rtp"
                ,"rtp"
                , "wild", "wilderness", "randomtp");
        this.plugin = plugin;
        registerSubCommand("reload", new ReloadCommand());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void execute(ConsoleCommandSender sender, String[] args) {
        if (!(args.length > 0)) return;

        Player player = RequisiteRTP.getInstance().getServer().getPlayer(args[0]);
        if (player == null) return;
        plugin.getRtpManager().addToQueue(player);
        MessageUtils.sendMessage(sender, "&aYou have been added to the queue to teleport to a random location!");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length > 0) {
            player = RequisiteRTP.getInstance().getServer().getPlayer(args[0]);
        }
        if (player == null) return;
        plugin.getRtpManager().addToQueue(player);
    //    MessageUtils.sendMessage(player, "&aYou have been added to the queue to teleport to a random location!");
    }
}
