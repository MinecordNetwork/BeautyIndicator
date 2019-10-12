package net.minecord.beautyindicator.controller

import net.minecord.beautyindicator.BeautyIndicator
import net.minecord.beautyindicator.colored
import net.minecord.beautyindicator.model.Combat
import org.bukkit.ChatColor
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap

class CombatController(private val beautyIndicator: BeautyIndicator, config: FileConfiguration) {
    private lateinit var combatControlling: BukkitTask
    private val entitiesInCombat = ConcurrentHashMap<LivingEntity, Combat>()
    private var character: String? = null
    private var showTime: Int = 0
    private var neutralColor: String? = null
    private lateinit var excludedMobs: MutableList<String>
    private lateinit var excludedWorlds: MutableList<String>
    var isHitByItself: Boolean = false
        private set
    private var activeColorMultiple: Boolean = false
    private var firstActiveColor: String? = null
    private var secondActiveColor: String? = null
    private var thirdActiveColor: String? = null

    init {
        onLoad(config)
    }

    fun onDisable() {
        entitiesInCombat.forEach { (entity, entityCombat) -> entity.customName = entityCombat.nameToRestore }
        entitiesInCombat.clear()
        combatControlling.cancel()
    }

    fun onReload(config: FileConfiguration) {
        onDisable()
        onLoad(config)
    }

    private fun onLoad(config: FileConfiguration) {
        character = config.getString("heart-character")
        showTime = config.getInt("show-time")
        thirdActiveColor = config.getString("active-color")
        neutralColor = config.getString("neutral-color")
        excludedMobs = config.getStringList("excluded-mobs")
        excludedMobs.replaceAll { it.toUpperCase() }
        excludedWorlds = config.getStringList("excluded-worlds")
        excludedWorlds.replaceAll { it.toLowerCase() }
        isHitByItself = config.getBoolean("hit-by-itself")
        activeColorMultiple = config.getBoolean("active-color-multiple.enabled")
        if (activeColorMultiple) {
            firstActiveColor = config.getString("active-color-multiple.one-third")
            secondActiveColor = config.getString("active-color-multiple.two-thirds")
            thirdActiveColor = config.getString("active-color-multiple.three-thirds")
        }

        startControllingCombat()
    }

    private fun startControllingCombat() {
        combatControlling = object : BukkitRunnable() {
            override fun run() {
                entitiesInCombat.forEach { (entity, entityCombat) ->
                    if (entityCombat.seconds > 0)
                        entityCombat.doUpdate()
                    else {
                        removeFromCombat(entity)
                    }
                }
            }
        }.runTaskTimerAsynchronously(beautyIndicator, 0, 20)
    }

    private fun addToCombat(entity: LivingEntity, entityName: String?) {
        if (entity.isDead)
            return

        val combat = entitiesInCombat[entity]
        if (combat == null) {
            entitiesInCombat[entity] = Combat(entityName, showTime)
        } else {
            combat.resetSeconds()
        }
    }

    fun removeFromCombat(entity: LivingEntity) {
        val combat = entitiesInCombat.remove(entity)
        entity.customName = combat?.nameToRestore
        if (entity.customName == null)
            entity.isCustomNameVisible = false
    }

    fun onHit(livingEntity: LivingEntity) {
        if (livingEntity.type.toString() in excludedMobs || livingEntity.world.name.toLowerCase() in excludedWorlds)
            return

        if (livingEntity.health <= 0 || livingEntity.isDead) {
            removeFromCombat(livingEntity)
            return
        }

        object : BukkitRunnable() {
            override fun run() {
                val hearts = buildString {
                    var multiplier = beautyIndicator.config.getDouble("mob-multipliers." + livingEntity.type.toString())
                    if (multiplier == 0.0) {
                        multiplier = 1.0
                    }

                    val maxHealth =
                        (livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value.toInt() / 2 * multiplier).toInt()
                    val newHealth = (livingEntity.health.toInt() / 2 * multiplier).toInt()
                    val leftHealth = maxHealth - newHealth

                    var newColor = thirdActiveColor

                    if (activeColorMultiple) {
                        if (newHealth <= maxHealth * 0.33)
                            newColor = firstActiveColor
                        else if (newHealth <= maxHealth * 0.66)
                            newColor = secondActiveColor
                    }

                    for (i in newHealth downTo 1)
                        append(newColor).append(character)
                    for (i in leftHealth downTo 1)
                        append(neutralColor).append(character)
                    append(" ")
                }

                addToCombat(livingEntity, if (livingEntity.customName == null) livingEntity.customName else if (ChatColor.stripColor(livingEntity.customName) == ChatColor.stripColor(hearts)) null else livingEntity.customName)
                livingEntity.customName = hearts.colored()
                livingEntity.isCustomNameVisible = true
            }
        }.runTaskAsynchronously(beautyIndicator)
    }
}
