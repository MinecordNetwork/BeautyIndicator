package net.minecord.beautyindicator.controller

import net.minecord.beautyindicator.BeautyIndicator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.scheduler.BukkitRunnable

class WorldController(private val beautyIndicator: BeautyIndicator, private val config: FileConfiguration) {
    init {
        fixBrokenNames()
    }

    fun onReload() {
        fixBrokenNames()
    }

    private fun fixBrokenNames() {
        val character = config.getString("heart-character")!!

        object : BukkitRunnable() {
            override fun run() {
                for (world in Bukkit.getWorlds()) {
                    for (entity in world.livingEntities) {
                        if (!beautyIndicator.combatController.isInCombat(entity)) {
                            val name = entity.customName
                            if (name != null && name.contains(character)) {
                                if (ChatColor.stripColor(name)?.replace(character, "")?.replace(" ", "") == "") {
                                    entity.customName = null
                                    entity.isCustomNameVisible = false
                                }
                            }
                        }
                    }
                }
            }
      }.runTaskTimerAsynchronously(beautyIndicator, 20, 120*20)
    }
}
