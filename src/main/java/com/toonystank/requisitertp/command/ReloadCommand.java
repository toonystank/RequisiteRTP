package com.toonystank.requisitertp.command;

import java.util.ArrayList;
import java.util.List;

import com.toonystank.requisitertp.RequisiteRTP;
import org.bukkit.command.CommandSender;

import com.toonystank.requisitertp.manager.SubCommand;
import com.toonystank.requisitertp.utils.Handlers;

public class ReloadCommand implements SubCommand {


    @Override
    public void execute(CommandSender sender, String[] args) {
        RequisiteRTP.getInstance().reload();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean hasBasePermission(CommandSender sender) {
        return Handlers.hasPermission(sender,"reload") 
        || Handlers.hasPermission(sender, "admin");
    }

}
