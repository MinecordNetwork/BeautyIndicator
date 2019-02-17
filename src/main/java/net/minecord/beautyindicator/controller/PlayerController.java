package net.minecord.beautyindicator.controller;

import net.minecord.beautyindicator.BeautyIndicator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerController {

    private BeautyIndicator beautyIndicator;

    public PlayerController(BeautyIndicator beautyIndicator) {
        this.beautyIndicator = beautyIndicator;
    }

    public LivingEntity getEntityLookingAt(Player player) {
        for (Entity entity : player.getNearbyEntities(25, 10, 25)) {
            if (!(entity instanceof LivingEntity))
                continue;

            Vector toEntity = entity.getLocation().toVector().subtract(player.getLocation().toVector());
            Vector direction = player.getLocation().getDirection();
            double dot = toEntity.normalize().dot(direction);

            if (dot >= 0.985)
                return (LivingEntity) entity;
        }

        return null;
    }
}
