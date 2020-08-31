package nl.parrotlync.discovshows.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {

    public static void sendMessage(CommandSender sender, String msg, boolean withPrefix) {
        if (withPrefix) {
            msg = "§8[§3Shows§8] " + msg;
        }
        sender.sendMessage(msg);
    }

    public static void broadcastMessage(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }
}
