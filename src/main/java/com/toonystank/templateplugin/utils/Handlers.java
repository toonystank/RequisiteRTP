package com.toonystank.templateplugin.utils;

import org.bukkit.command.CommandSender;

import com.toonystank.templateplugin.TemplatePlugin;

public class Handlers {

    public static boolean hasPermission(CommandSender sender, String highLevelPermission) {
        return sender.hasPermission(TemplatePlugin.getInstance().getPluginName() + "." + highLevelPermission);
    }

}
