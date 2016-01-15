package me.phal.wrench;

import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin
{
  public static Main plugin;
  public static Economy economy = null;
  public static Main instance;

  public void onEnable()
  {
    instance = this;
    plugin = this;
    getServer().getPluginManager().registerEvents(new MainListener(), this);
    if (!setupEconomy()) {
      getServer().getLogger().log(Level.SEVERE, ChatColor.RED + "Wrench could not hook into vault");
      getConfig().options().copyDefaults(true);
      saveDefaultConfig();
    }
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "You need to be a player to do this!");
      return false;}
    Player p = (Player)sender;
    if ((args.length == 1) && (args[0].equalsIgnoreCase("get")))
    {
      if (p.hasPermission("wrench.get"))
      {
        if (p.getInventory().firstEmpty() != -1)
          p.getInventory().addItem(new ItemStack[] { getWrench() });
        else {
          p.sendMessage(ChatColor.DARK_RED + "Your inventory is full!");
        }
      }
      else {
        p.sendMessage(ChatColor.DARK_RED + "You do not have permission to do this!");
      }
    }
    else {
      displayHelp(sender);
    }
    return false;
    }
  
  private void displayHelp(CommandSender s)
  {
    s.sendMessage(ChatColor.DARK_RED + "Use like /wrench get to get a wrench");
  }

  private boolean setupEconomy()
  {
    RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(
      Economy.class);
    if (economyProvider != null) {
      economy = (Economy)economyProvider.getProvider();
    }
    return economy != null;
  }

  public static ItemStack getWrench()
  {
    String wrench = instance.getConfig().getString("WrenchMaterial");
    ItemStack is = new ItemStack(Material.getMaterial(wrench));
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.RED + "Wrench");
    is.setItemMeta(im);
    return is;
  }

  public static Main getInstance()
  {
    return instance;
  }
}
