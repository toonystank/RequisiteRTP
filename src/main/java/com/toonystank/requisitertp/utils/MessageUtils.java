package com.toonystank.requisitertp.utils;

import com.toonystank.requisitertp.RequisiteRTP;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageUtils {

    private static BukkitAudiences audience;

    public MessageUtils(BukkitAudiences bukkitAudiences) {
        audience = bukkitAudiences;
    }


    public static void sendMessage(List<Player> sender, String message) {
        if (sender.isEmpty()) return;

        Set<Player> playersSentMessage = new HashSet<>(); 

        for (Player player : sender) {
            if (player == null || playersSentMessage.contains(player)) {
                continue;
            }

            sendMessage(player, message);
            playersSentMessage.add(player);
        }
    }

    public static void sendMessage(Player sender, String message) {
        if (!sender.getPlayer().isOnline()) return;
        sendMessage(sender,message);
    }
    public static void sendMessage(List<Player> sender, String message,boolean titleMessage) {
        if (sender.isEmpty()) return;
        Set<Player> playersSentMessage = new HashSet<>();

        for (Player Player : sender) {
            if (Player == null || playersSentMessage.contains(Player)) {
                continue;
            }

            sendMessage(Player, message, titleMessage);
            playersSentMessage.add(Player);
        }
    }
    public static void sendMessage(Player sender, String message,boolean titleMessage) {
        sendMessage((CommandSender) sender, message,titleMessage);
    }
    public static void sendMessage(CommandSender sender, String message,boolean titleMessage) {
        if (!titleMessage) {
            sendMessage(sender,message);
            return;
        }
        if (!(sender instanceof Player)) {
            sendMessage(sender,message);
            return;
        }
        final Player player = (Player) sender;
        final Component mainTitle = format(message);
        final Title title = Title.title(mainTitle, Component.empty());
        audience.sender(player).showTitle(title);
    }
    public static void sendMessage(CommandSender sender, String message) {
        MessageUtils.toConsole(message  + "  sending to player " + sender ,true );
        if (RequisiteRTP.getInstance().getMainConfig().isSmallText()) {
            message = SmallLetterConvertor.convert(message);
        }
        Component component = new MineDown(message).toComponent();
        component = component.decoration(TextDecoration.ITALIC, false);
        audience.sender(sender).sendMessage(component);
    }
    public static void sendMessage(Player sender, Component message) {
        sendMessage((CommandSender) sender, message);
    }
    public static void sendMessage(CommandSender sender, Component message) {
        message = message.decoration(TextDecoration.ITALIC, false);
        audience.sender(sender).sendMessage(message);
    }
    public static @NotNull List<Component> format(List<String> list) {
        return list.stream().map(MessageUtils::format).collect(Collectors.toList());
    }
    public static @NotNull Component format(String message) {
        return format(message,false);
    }
    public static @NotNull Component format(String message,boolean smallFont) {
        if (smallFont) message = SmallLetterConvertor.convert(message);
        Component component = new MineDown(message).toComponent();
        component = component.decoration(TextDecoration.ITALIC, false);
        return component;
    }
    public static String formatString(String message) {
        if (message == null) return "null";
        return formatString(message,false);
    }
    public static BaseComponent[] formatString(String message,int i) {
        return de.themoep.minedown.MineDown.parse(message);
    }
    public static String formatString(String message,boolean smallFont) {
        if (smallFont) message = SmallLetterConvertor.convert(message);
        BaseComponent[] baseComponents = de.themoep.minedown.MineDown.parse(message);
        return TextComponent.toLegacyText(baseComponents);
    }
    public static void toConsole(List<String> list, boolean string) {
        list.forEach(message -> toConsole(message,false));
    }
    public static void toConsole(List<Component> list) {
        list.forEach(component -> toConsole(component, false));
    }
    public static void toConsole(String message, boolean debug) {
        if (debug) {
            if (!RequisiteRTP.getInstance().getMainConfig().isDebug()) return;
        }
        message = "&a[RequisiteRTP]&r " + message;
        Component component = new MineDown(message).toComponent();
        toConsole(component, debug);
    }
    public static void toConsole(Component component, boolean debug) {
        if (debug) {
            if (!RequisiteRTP.getInstance().getMainConfig().isDebug()) return;
        }
        component = component.decoration(TextDecoration.ITALIC,false);
        audience.sender(RequisiteRTP.getInstance().getServer().getConsoleSender()).sendMessage(component);
    }
    public static void error(String message) {
        message = message + ". Server version: " + RequisiteRTP.getInstance().getServer().getVersion() + ". Plugin version: " + RequisiteRTP.getInstance().getDescription().getVersion() + ". Please report this error to the plugin developer.";
        message = "[" + RequisiteRTP.getInstance().getPluginName()+ "] " + message;
        Component component = new MineDown(message).toComponent();
        error(component);
    }
    public static void error(Component component) {
        try {
            component = component.decoration(TextDecoration.ITALIC, false);
            component = component.color(TextColor.fromHexString("#CF203E"));
            audience.sender(RequisiteRTP.getInstance().getServer().getConsoleSender()).sendMessage(component);
        } catch (NullPointerException ignored) {
            error("an error occurred while sending a message");
        }
    }
    public static void debug(String message) {
        if (!RequisiteRTP.getInstance().getMainConfig().isDebug()) return;
        message = message + ". Server version: " + RequisiteRTP.getInstance().getServer().getVersion() + ". Plugin version: " + RequisiteRTP.getInstance().getDescription().getVersion() + ". To stop receiving this messages please update your config.yml";
        Component component = new MineDown(message).toComponent();
        debug(component);
    }
    public static void debug(Component component) {
        try {
            component = component.decoration(TextDecoration.ITALIC, false);
            audience.sender(RequisiteRTP.getInstance().getServer().getConsoleSender()).sendMessage(component);
        } catch (NullPointerException ignored) {
            error("an error occurred while sending a message");
        }
    }
    public static void warning(String message) {
        message = "[" + RequisiteRTP.getInstance().getPluginName()+ "] " + message;
        Component component = new MineDown(message).toComponent();
        warning(component);
    }
    public static void warning(Component component) {
        component = component.decoration(TextDecoration.ITALIC,false);
        component = component.color(TextColor.fromHexString("#FFC107"));
        audience.sender(RequisiteRTP.getInstance().getServer().getConsoleSender()).sendMessage(component);
    }
    public static String replaceGrayWithWhite(String inputString) {
        if (inputString.contains("&7")) inputString = inputString.replace("&7", "&f");
        return inputString;
    }
}
