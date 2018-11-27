package net.minecord.beautyindicator.command;

import net.minecord.beautyindicator.BeautyIndicator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private BeautyIndicator beautyIndicator;

    public ReloadCommand(BeautyIndicator beautyIndicator) {
        this.beautyIndicator = beautyIndicator;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.hasPermission("beautyindicator.reload") && strings.length > 0 && strings[0].equals("reload")) {
            beautyIndicator.onReload();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &aPlugin was successfully reloaded"));
            return true;
        }
        return false;
    }
}