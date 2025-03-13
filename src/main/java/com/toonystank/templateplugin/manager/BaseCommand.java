package com.toonystank.templateplugin.manager;

import com.toonystank.templateplugin.TemplatePlugin;
import com.toonystank.templateplugin.utils.Handlers;
import com.toonystank.templateplugin.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

@Getter
@SuppressWarnings("unused")
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private final TemplatePlugin plugin;
    private final Command commandData;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    protected BaseCommand(TemplatePlugin plugin, String name, boolean playerOnlyCommand, boolean requireArgument, String description, String usage, String permission, List<String> aliases) {
        this.plugin = plugin;
        this.commandData = new Command(name, playerOnlyCommand, requireArgument, description, usage, permission, aliases);
        registerCommand(commandData);
    }
    protected BaseCommand(TemplatePlugin plugin, String name, boolean playerOnlyCommand, boolean requireArgument, String description, String usage, String permission, String... aliases) {
        this.plugin = plugin;
        this.commandData = new Command(name, playerOnlyCommand, requireArgument, description, usage, permission, Arrays.asList(aliases));
        registerCommand(commandData);
    }

    protected BaseCommand(TemplatePlugin plugin, Command commandData) {
        this.plugin = plugin;
        this.commandData = commandData;
        registerCommand(commandData);
    }

    public void registerSubCommand(String name, SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    public abstract void execute(ConsoleCommandSender sender, String[] args);

    public abstract void execute(Player player, String[] args);

    private void registerCommand(Command commandData) {
        PluginCommand command = plugin.getCommand(commandData.name());
        if (command == null) {
            command = createPluginCommand(commandData);
        }
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
            if (commandData.aliases() != null && !commandData.aliases().isEmpty()) {
                command.setAliases(commandData.aliases());
            }
            if (commandData.description() != null && !commandData.description().isEmpty()) {
                command.setDescription(commandData.description());
            }
            if (commandData.usage() != null && !commandData.usage().isEmpty()) {
                command.setUsage(commandData.usage());
            }
            command.setPermission(commandData.permission());
        } else {
            MessageUtils.warning("Failed to register command: " + commandData.name());
        }
    }

    private PluginCommand createPluginCommand(Command commandData) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance(commandData.name(), plugin);
            command.setExecutor(this);
            command.setTabCompleter(this);
            Field commandMapField = plugin.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(plugin.getServer().getPluginManager());
            commandMap.register(plugin.getName(), command);
            return command;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandData.playerOnlyCommand() && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMainConfig().getLanguageConfig().getPlayerOnly());
            return true;
        }
        if (commandData.permission() != null && !Handlers.hasPermission(sender, commandData.permission())) {
            sender.sendMessage(plugin.getMainConfig().getLanguageConfig().getNoPermission());
            return true;
        }

        // Handle subcommands
        if (args.length > 0) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand == null) return false;
            if (!subCommand.hasBasePermission(sender)) return false;
            subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
            
        }

        // Default behavior
        if (sender instanceof Player) {
            execute((Player) sender, args);
        } else if (sender instanceof ConsoleCommandSender) {
            execute((ConsoleCommandSender) sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(subCommands.keySet());
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                return subCommand.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return Collections.emptyList();
    }

    public static class Command {
        private final String name;
        private final boolean playerOnlyCommand;
        private final boolean requireArguments;
        private final String description;
        private final String usage;
        private final String permission;
        private final List<String> aliases;

        public Command(String name, boolean playerOnlyCommand, boolean requireArguments, String description, String usage, String permission, List<String> aliases) {
            this.name = name;
            this.playerOnlyCommand = playerOnlyCommand;
            this.requireArguments = requireArguments;
            this.description = description;
            this.usage = usage;
            this.permission = permission;
            this.aliases = aliases;
        }

        public String name() { return name; }
        public boolean playerOnlyCommand() { return playerOnlyCommand; }
        public boolean requireArguments() { return requireArguments; }
        public String description() { return description; }
        public String usage() { return usage; }
        public String permission() { return permission; }
        public List<String> aliases() { return aliases; }
    }
}
