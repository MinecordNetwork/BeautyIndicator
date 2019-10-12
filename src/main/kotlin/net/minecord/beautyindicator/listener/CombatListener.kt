package net.minecord.beautyindicator.listener

import net.minecord.beautyindicator.BeautyIndicator
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable

class CombatListener(private val beautyIndicator: BeautyIndicator) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityHitByEntity(e: EntityDamageByEntityEvent) {
        if (e.isCancelled) return

        val entity = e.entity
        if (entity !is LivingEntity || entity is ArmorStand) return
        if (!beautyIndicator.combatController.isHitByItself)
            beautyIndicator.combatController.onHit(entity)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityHit(e: EntityDamageEvent) {
        if (e.isCancelled) return

        val entity = e.entity
        if (entity !is LivingEntity || entity is ArmorStand) return
        if (beautyIndicator.combatController.isHitByItself)
            beautyIndicator.combatController.onHit(entity)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityHitByEntityCheck(e: EntityDamageByEntityEvent) {
        if (!(e.entity is LivingEntity || e.entity is ArmorStand)) return
        if (!beautyIndicator.combatController.isHitByItself) {
            val livingEntity = e.entity as LivingEntity
            if (livingEntity.health <= 0) {
                beautyIndicator.combatController.removeFromCombat(livingEntity)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityHitCheck(e: EntityDamageEvent) {
        val entity = e.entity as? LivingEntity ?: return

        if (beautyIndicator.combatController.isHitByItself) {
            if (entity.health <= 0)
                beautyIndicator.combatController.removeFromCombat(entity)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDeath(e: EntityDeathEvent) {
        beautyIndicator.combatController.removeFromCombat(e.entity)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val event = e.entity.lastDamageCause
        if (event is EntityDamageByEntityEvent) {
            var killer = event.damager
            if (killer is Projectile) {
                killer = killer.shooter as LivingEntity
            }
            if (killer is LivingEntity) {
                beautyIndicator.combatController.removeFromCombat(killer)
            }
        }
    }

    @EventHandler
    fun onEntityAim(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_AIR) {
            if (e.player.inventory.itemInMainHand.type == Material.BOW || e.player.inventory.itemInOffHand.type == Material.BOW) {
                if (e.player.inventory.contains(Material.ARROW)) {
                    val nearbyEntities = e.player.getNearbyEntities(25.0, 10.0, 25.0)
                    object : BukkitRunnable() {
                        override fun run() {
                            val livingEntity = beautyIndicator.playerController.getEntityLookingAt(e.player, nearbyEntities)
                            if (livingEntity != null)
                                beautyIndicator.combatController.onHit(livingEntity)
                        }
                    }.runTaskAsynchronously(beautyIndicator)
                }
            }
        }
    }
}
