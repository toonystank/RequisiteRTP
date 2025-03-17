package com.toonystank.requisitertp.command;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.utils.Handlers;
import com.toonystank.requisitertp.utils.MessageUtils;
import lombok.Getter;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

@Getter
@SuppressWarnings("unused")
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private final RequisiteRTP plugin;
    private final Command commandData;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    protected BaseCommand(RequisiteRTP plugin, String name, boolean playerOnlyCommand, boolean requireArgument, String description, String usage, String permission, List<String> aliases) {
        this.plugin = plugin;
        this.commandData = new Command(name, playerOnlyCommand, requireArgument, description, usage, permission, aliases);
        registerCommand(commandData);
    }
    protected BaseCommand(RequisiteRTP plugin, String name, boolean playerOnlyCommand, boolean requireArgument, String description, String usage, String permission, String... aliases) {
        this.plugin = plugin;
        this.commandData = new Command(name, playerOnlyCommand, requireArgument, description, usage, permission, Arrays.asList(aliases));
        registerCommand(commandData);
    }

    protected BaseCommand(RequisiteRTP plugin, Command commandData) {
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

    public void registerCommand(Command commandData) {
        PluginCommand command = createPluginCommand(commandData);
        if (command != null) {
            MessageUtils.toConsole("Registered command: " + commandData.getName(),true);
            // Set executor and tab completer
            command.setExecutor(this);
            command.setTabCompleter(this);

            // Set aliases if provided
            if (commandData.getAliases() != null && !commandData.getAliases().isEmpty()) {
                command.setAliases(commandData.getAliases());
            }

            // Set description if provided
            if (commandData.getDescription() != null && !commandData.getDescription().isEmpty()) {
                command.setDescription(commandData.getDescription());
            }

            // Set usage if provided
            if (commandData.getUsage() != null && !commandData.getUsage().isEmpty()) {
                command.setUsage(commandData.getUsage());
            }

            // Set permission if provided
            command.setPermission(commandData.getPermission());
        } else {
            System.err.println("Failed to register command: " + commandData.getName());
        }
    }

    private PluginCommand createPluginCommand(Command commandData) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class
                    .getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance(commandData.getName(), plugin);
            command.setExecutor(this);
            command.setTabCompleter(this);
            if (commandData.getDescription() != null) {
                command.setDescription(commandData.getDescription());
            }
            if (commandData.getUsage() != null) {
                command.setUsage(commandData.getUsage());
            }
            if (commandData.getPermission() != null) {
                command.setPermission(commandData.getPermission());
            }
            if (commandData.getAliases() != null) {
                command.setAliases(commandData.getAliases());
            }
            // Use reflection to access the CommandMap
            CommandMap commandMap = getCommandMap();
            commandMap.register(plugin.getName(), command);
            return command;
        } catch (Exception e) {
            MessageUtils.toConsole("Failed to register command: " + commandData.getName(),true);
            e.printStackTrace();
            return null;
        }
    }

    private CommandMap getCommandMap() throws NoSuchFieldException, IllegalAccessException {
        MessageUtils.toConsole("Getting CommandMap",true);
        Field commandMapField;
        try {
            // Try to get the CommandMap from CraftServer (for newer versions)
            commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            MessageUtils.toConsole("Getting CommandMap from CraftServer",true);
        } catch (NoSuchFieldException e) {
            // Fallback to SimplePluginManager (for older versions)
            commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            MessageUtils.toConsole("Getting CommandMap from SimplePluginManager",true);
        }
        commandMapField.setAccessible(true);

        // Get the CommandMap instance
        CommandMap commandMap;
        if (commandMapField.getDeclaringClass() == SimplePluginManager.class) {
            // For older versions (1.8.8 - 1.12)
            commandMap = (CommandMap) commandMapField.get(plugin.getServer().getPluginManager());
        } else {
            // For newer versions (1.13+)
            commandMap = (CommandMap) commandMapField.get(plugin.getServer());
        }
        commandMapField.setAccessible(true);
        return commandMap;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MessageUtils.toConsole("Command: " + command.getName() + " Args: " + Arrays.toString(args),true);
        if (commandData.getPlayerOnlyCommand() && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMainConfig().getLanguageConfig().getPlayerOnly());
            return true;
        }
        if (commandData.getPermission() != null && !Handlers.hasPermission(sender, commandData.getPermission())) {
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

    @Getter
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

        public boolean getPlayerOnlyCommand() { return playerOnlyCommand; }
    }
}
