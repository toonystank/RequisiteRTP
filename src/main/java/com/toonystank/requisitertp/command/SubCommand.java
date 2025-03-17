package com.toonystank.requisitertp.command;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface SubCommand {

    void execute(CommandSender sender, String[] args);

    List<String> onTabComplete(CommandSender sender, String[] args);

    boolean hasBasePermission(CommandSender sender);

}