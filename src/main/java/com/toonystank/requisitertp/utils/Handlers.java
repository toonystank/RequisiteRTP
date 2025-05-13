package com.toonystank.requisitertp.utils;

import com.toonystank.requisitertp.RequisiteRTP;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

public class Handlers {

    public static boolean hasPermission(CommandSender sender, String highLevelPermission) {
        return sender.hasPermission(RequisiteRTP.getInstance().getPluginName() + "." + highLevelPermission);
    }

    public static  class Legacy {
        public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();

        private Legacy() {
            throw new UnsupportedOperationException("Class should not be instantiated!");
        }
    }

}
