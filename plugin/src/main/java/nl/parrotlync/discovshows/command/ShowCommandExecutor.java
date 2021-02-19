package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import nl.parrotlync.discovshows.util.ChatUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ShowCommandExecutor implements TabExecutor {
    private final TreeMap<String, ShowCommand> commands = new TreeMap<>();

    public ShowCommandExecutor() {
        commands.put("start", new StartCommand());
        commands.put("stop", new StopCommand());
        commands.put("list", new ListCommand());
        commands.put("forcestart", new ForceStartCommand());
        commands.put("schedules", new SchedulesCommand());
        commands.put("reload", new ReloadCommand());
        commands.put("fallingblock", new FallingBlockCommand());
        if (Bukkit.getPluginManager().isPluginEnabled("LightAPI") ) { commands.put("light", new LightCommand()); }
        if (DiscovShows.getInstance().getWorldEditWrapper() != null) { commands.put("paste", new PasteCommand()); }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6DiscovShows-" + DiscovShows.getInstance().getDescription().getVersion() +" §7(§aParrotLync§7) - Use /show help");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            return help(sender);
        }

        ShowCommand showCommand = commands.get(args[0]);
        if (showCommand == null) {
            ChatUtil.sendConfigMessage(sender, "subcommand-not-found");
            return true;
        }

        if (!sender.hasPermission(showCommand.getPermission())) {
            ChatUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        String[] commandArgs = (String[]) ArrayUtils.remove(args, 0);
        if (commandArgs.length < showCommand.getArguments().size()) {
            List<String> missingArguments = new ArrayList<>();
            for (TabArgument argument : new ArrayList<>(showCommand.getArguments().values()).subList(commandArgs.length, showCommand.getArguments().size())) {
                missingArguments.add(argument.getName());
            }
            ChatUtil.sendMissingArguments(sender, missingArguments.toArray(new String[0]));
            return true;
        }

        showCommand.execute(sender, commandArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (String commandName : commands.keySet()) {
                if (sender.hasPermission(commands.get(commandName).getPermission())) {
                    suggestions.add(commandName);
                }
            }
            StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        if (args.length > 1) {
            ShowCommand showCommand = commands.get(args[0]);
            if (showCommand != null && sender.hasPermission(showCommand.getPermission())) {
                TabArgument argument = showCommand.getArguments().get(args.length - 2);
                if (argument != null) {
                    if (argument.isPersonal()) {
                        suggestions.addAll(argument.getSuggestions(sender));
                    } else {
                        suggestions.addAll(argument.getSuggestions());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[args.length - 1], suggestions, new ArrayList<>());
        }

        return suggestions;
    }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("discovshows.command.help")) {
            sender.sendMessage("§f+---+ §9DiscovShows §f+---+");
            for (String commandName : commands.keySet()) {
                ShowCommand showCommand = commands.get(commandName);
                if (sender.hasPermission(showCommand.getPermission())) {
                    StringBuilder args = new StringBuilder();
                    for (TabArgument argument : showCommand.getArguments().values()) {
                        args.append(String.format(" <%s>", argument.getName()));
                    }
                    sender.sendMessage(String.format("§3/show %s%s - §7%s", commandName, args.toString(), showCommand.getDescription()));
                }
            }
        } else {
            ChatUtil.sendConfigMessage(sender, "no-permission");
        }
        return true;
    }
}
