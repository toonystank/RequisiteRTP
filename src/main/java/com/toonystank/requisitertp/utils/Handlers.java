package com.toonystank.requisitertp.utils;

import com.toonystank.requisitertp.RequisiteRTP;
import org.bukkit.command.CommandSender;

public class Handlers {

    public static boolean hasPermission(CommandSender sender, String highLevelPermission) {
        return sender.hasPermission(RequisiteRTP.getInstance().getPluginName() + "." + highLevelPermission);
    }

}
