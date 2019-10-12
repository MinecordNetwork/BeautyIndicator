package net.minecord.beautyindicator.command

import net.minecord.beautyindicator.BeautyIndicator
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ReloadCommand(private val beautyIndicator: BeautyIndicator) : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender.hasPermission("net.minecord.beautyindicator.reload") && strings.isNotEmpty() && strings[0] == "reload") {
            beautyIndicator.onReload()
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[BeautyIndicator] &aPlugin was successfully reloaded"))
            return true
        }
        return false
    }
}
