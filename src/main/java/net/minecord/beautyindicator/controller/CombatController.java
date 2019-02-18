package net.minecord.beautyindicator.controller;

import net.minecord.beautyindicator.BeautyIndicator;
import net.minecord.beautyindicator.model.Combat;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CombatController implements Listener {

    private BeautyIndicator beautyIndicator;
    private BukkitTask combatControlling;
    private ConcurrentHashMap<LivingEntity, Combat> entitiesInCombat = new ConcurrentHashMap<>();
    private String character;
    private int showTime;
    private String neutralColor;
    private List<String> excludedMobs;
    private List<String> excludedWorlds;
    private boolean hitByItself;
    private boolean activeColorMultiple;
    private String firstActiveColor;
    private String secondActiveColor;
    private String thirdActiveColor;

    public CombatController(BeautyIndicator beautyIndicator, FileConfiguration config) {
        this.beautyIndicator = beautyIndicator;

        onLoad(config);
    }

    public void onDisable() {
        entitiesInCombat.forEach((entity, entityCombat) -> entity.setCustomName(entityCombat.getNameToRestore()));
        entitiesInCombat.clear();
        combatControlling.cancel();
    }

    public void onReload(FileConfiguration config) {
        onDisable();
        onLoad(config);
    }

    private void onLoad(FileConfiguration config) {
        character = config.getString("heart-character");
        showTime = config.getInt("show-time");
        thirdActiveColor = config.getString("active-color");
        neutralColor = config.getString("neutral-color");
        excludedMobs = config.getStringList("excluded-mobs");
        excludedMobs.replaceAll(String::toUpperCase);
        excludedWorlds = config.getStringList("excluded-worlds");
        excludedWorlds.replaceAll(String::toLowerCase);
        hitByItself = config.getBoolean("hit-by-itself");
        activeColorMultiple = config.getBoolean("active-color-multiple.enabled");
        if (activeColorMultiple) {
            firstActiveColor = config.getString("active-color-multiple.one-third");
            secondActiveColor = config.getString("active-color-multiple.two-thirds");
            thirdActiveColor = config.getString("active-color-multiple.three-thirds");
        }

        startControllingCombat();
    }

    private void startControllingCombat() {
        combatControlling = new BukkitRunnable() {
            @Override
            public void run() {
                entitiesInCombat.forEach((entity, entityCombat) -> {
                    if (entityCombat.getSeconds() > 0)
                        entityCombat.doUpdate();
                    else {
                        removeFromCombat(entity);
                    }
                });
            }
        }.runTaskTimerAsynchronously(beautyIndicator, 0, 20);
    }

    private void addToCombat(LivingEntity entity, String entityName) {
        if (entity.isDead())
            return;
        if (!entitiesInCombat.containsKey(entity))
            entitiesInCombat.put(entity, new Combat(entityName, showTime));
        else
            entitiesInCombat.get(entity).resetSeconds();
    }

    public void removeFromCombat(LivingEntity entity) {
        if (entity != null && entitiesInCombat.containsKey(entity)) {
            String customName = entitiesInCombat.get(entity).getNameToRestore();
            entity.setCustomName(customName);
            if (customName == null)
                entity.setCustomNameVisible(false);
            entitiesInCombat.remove(entity);
        }
    }

    public void onHit(Entity entity) {
        if (excludedMobs.contains(entity.getType().toString()) || excludedWorlds.contains(entity.getWorld().getName().toLowerCase()))
            return;

        LivingEntity livingEntity = (LivingEntity) entity;
        if (livingEntity.getHealth() <= 0 || livingEntity.isDead()) {
            removeFromCombat(livingEntity);
            return;
        }

        new BukkitRunnable() {
            public void run() {
                StringBuilder hearts = new StringBuilder();

                double multiplier = beautyIndicator.getConfig().getDouble("mob-multipliers." + entity.getType().toString());
                if (multiplier == 0) {
                    multiplier = 1;
                }

                int maxHealth = (int) ((int) livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2 * multiplier);
                int newHealth = (int) ((int) livingEntity.getHealth() / 2 * multiplier);
                int leftHealth = maxHealth - newHealth;

                String newColor = thirdActiveColor;

                if (activeColorMultiple) {
                    if (newHealth <= maxHealth * 0.33)
                        newColor = firstActiveColor;
                    else if (newHealth <= maxHealth * 0.66)
                        newColor = secondActiveColor;
                }

                for (int i = newHealth; i > 0; i--)
                    hearts.append(newColor).append(character);
                for (int i = leftHealth; i > 0; i--)
                    hearts.append(neutralColor).append(character);
                hearts.append(" ");

                addToCombat(livingEntity, livingEntity.getCustomName() == null ? livingEntity.getCustomName() : ChatColor.stripColor(livingEntity.getCustomName()).equals(ChatColor.stripColor(hearts.toString())) ? null : livingEntity.getCustomName());

                entity.setCustomName(ChatColor.translateAlternateColorCodes('&', hearts.toString()));
                entity.setCustomNameVisible(true);
            }
        }.runTaskAsynchronously(beautyIndicator);
    }

    public boolean isHitByItself() {
        return hitByItself;
    }
}
