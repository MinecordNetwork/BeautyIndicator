package net.minecord.beautyindicator

import org.bukkit.ChatColor

fun String.colored(colorChar: Char = '&'): String {
    return ChatColor.translateAlternateColorCodes(colorChar, this)
}