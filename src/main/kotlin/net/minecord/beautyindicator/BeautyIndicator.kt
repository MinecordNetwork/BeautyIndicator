package net.minecord.beautyindicator

import net.minecord.beautyindicator.command.ReloadCommand
import net.minecord.beautyindicator.controller.CombatController
import net.minecord.beautyindicator.controller.PlayerController
import net.minecord.beautyindicator.controller.WorldController
import net.minecord.beautyindicator.listener.CombatListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class BeautyIndicator : JavaPlugin() {
    private val pluginPrefix = "&b[BeautyIndicator] &7"
    lateinit var combatController: CombatController
        private set
    lateinit var playerController: PlayerController
        private set
    lateinit var worldController: WorldController
        private set

    override fun onEnable() {
        saveDefaultConfig()

        combatController = CombatController(this, config)
        playerController = PlayerController()
        worldController = WorldController(this, config)

        getCommand("beautyindicator")!!.setExecutor(ReloadCommand(this))

        Bukkit.getConsoleSender().sendMessage("$pluginPrefix&aPlugin successfully enabled!".colored())
        Bukkit.getConsoleSender().sendMessage("${pluginPrefix}Spigot page: &ahttps://www.spigotmc.org/resources/.57546/".colored())
        Bukkit.getConsoleSender().sendMessage("${pluginPrefix}Author: &eRixafy &a[https://rixafy.pro]".colored())

        Bukkit.getPluginManager().registerEvents(CombatListener(this), this)
    }

    fun onReload() {
        saveDefaultConfig()
        reloadConfig()

        combatController.onReload(config)
        worldController.onReload()
    }

    override fun onDisable() {
        combatController.onDisable()

        Bukkit.getConsoleSender().sendMessage("$pluginPrefix&6Plugin successfully disabled!".colored())
    }
}
