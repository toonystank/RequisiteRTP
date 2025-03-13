package com.toonystank.templateplugin.command;

import java.util.ArrayList;
import java.util.List;

import com.toonystank.templateplugin.TemplatePlugin;
import org.bukkit.command.CommandSender;

import com.toonystank.templateplugin.manager.SubCommand;
import com.toonystank.templateplugin.utils.Handlers;

public class ReloadCommand implements SubCommand {


    @Override
    public void execute(CommandSender sender, String[] args) {
        TemplatePlugin.getInstance().reload();
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
