package net.minecord.beautyindicator.command

import net.minecord.beautyindicator.BeautyIndicator
import net.minecord.beautyindicator.colored
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ReloadCommand(private val beautyIndicator: BeautyIndicator) : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender.hasPermission("beautyindicator.reload") && strings.isNotEmpty() && strings[0] == "reload") {
            beautyIndicator.onReload()
            commandSender.sendMessage("&b[BeautyIndicator] &aPlugin was successfully reloaded".colored())
            return true
        }
        return false
    }
}
