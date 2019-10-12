package net.minecord.beautyindicator

import net.minecord.beautyindicator.command.ReloadCommand
import net.minecord.beautyindicator.controller.CombatController
import net.minecord.beautyindicator.controller.PlayerController
import net.minecord.beautyindicator.listener.CombatListener
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.command.Command
import org.bukkit.plugin.java.annotation.command.Commands
import org.bukkit.plugin.java.annotation.plugin.Description
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.Website
import org.bukkit.plugin.java.annotation.plugin.author.Author

@Plugin(name = "BeautyIndicator", version = "1.8")
@Description("Minecraft (Spigot/Bukkit) plugin for indicating mob health")
@Commands(Command(name = "net/minecord/beautyindicator", desc = "Help command"))
@Website("https://minecord.net")
@Author("Rixafy")
class BeautyIndicator : JavaPlugin() {
    private val pluginPrefix = "&b[BeautyIndicator] &7"
    lateinit var combatController: CombatController
        private set
    lateinit var playerController: PlayerController
        private set

    override fun onEnable() {
        saveDefaultConfig()

        combatController = CombatController(this, config)
        playerController = PlayerController()

        getCommand("beautyindicator")!!.executor = ReloadCommand(this)

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "$pluginPrefix&aPlugin successfully enabled!"))
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + "Spigot page: &ahttps://www.spigotmc.org/resources/.57546/"))
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + "Author: &eRixafy &a[https://rixafy.pro]"))

        Bukkit.getPluginManager().registerEvents(CombatListener(this), this)
    }

    fun onReload() {
        saveDefaultConfig()
        reloadConfig()

        combatController.onReload(config)
    }

    override fun onDisable() {
        combatController.onDisable()

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "$pluginPrefix&6Plugin successfully disabled!"))
    }
}
