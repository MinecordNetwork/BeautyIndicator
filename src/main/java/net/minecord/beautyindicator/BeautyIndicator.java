package net.minecord.beautyindicator;

import net.minecord.beautyindicator.command.ReloadCommand;
import net.minecord.beautyindicator.controller.CombatController;
import net.minecord.beautyindicator.controller.PlayerController;
import net.minecord.beautyindicator.listener.CombatListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;

@Plugin(name = "BeautyIndicator", version = "1.6")
@Description("Minecraft (Spigot/Bukkit) plugin for indicating mob health")
@Commands(@Command(name = "beautyindicator", desc = "Help command"))
@Website("https://minecord.net")
public class BeautyIndicator extends JavaPlugin {

    private final String pluginPrefix = "&b[BeautyIndicator] &7";
    private CombatController combatController;
    private PlayerController playerController;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        combatController = new CombatController(this, getConfig());
        playerController = new PlayerController();

        getCommand("beautyindicator").setExecutor(new ReloadCommand(this));

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + "&aPlugin successfully enabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + "Spigot page: &ahttps://www.spigotmc.org/resources/.57546/"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + "Author: &eRixafy &a[https://rixafy.pro]"));

        Bukkit.getPluginManager().registerEvents(new CombatListener(this), this);
    }

    public void onReload() {
        saveDefaultConfig();
        reloadConfig();

        combatController.onReload(getConfig());
    }

    @Override
    public void onDisable() {
        combatController.onDisable();

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + "&6Plugin successfully disabled!"));
    }

    public CombatController getCombatController() {
        return combatController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }
}
