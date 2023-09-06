package com.thgplugins.domination.core;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultAPI {

    private JavaPlugin plugin;
    private Economy economy;

    public VaultAPI(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!setupEconomy() ) {
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getDescription().getName()));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean hasBalance(Player player, double balance) {
        return economy.has(player, balance);
    }

    public void depositPlayer(Player player, double balance) {
        economy.depositPlayer(player, balance);
    }

    public void withdrawPlayer(Player player, double balance) {
        economy.withdrawPlayer(player, balance);
    }

    public double getBalance(Player player) {
        return economy.getBalance(player);
    }


}
