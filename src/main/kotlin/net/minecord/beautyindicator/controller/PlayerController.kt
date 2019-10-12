package net.minecord.beautyindicator.controller

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class PlayerController {
    fun getEntityLookingAt(player: Player): LivingEntity? {
        for (entity in player.getNearbyEntities(25.0, 10.0, 25.0)) {
            if (entity !is LivingEntity)
                continue

            val toEntity = entity.getLocation().toVector().subtract(player.location.toVector())
            val direction = player.location.direction
            val dot = toEntity.normalize().dot(direction)

            if (dot >= 0.985)
                return entity
        }

        return null
    }
}
