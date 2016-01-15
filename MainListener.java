package me.phal.wrench;

	import net.milkbowl.vault.economy.Economy;
	import org.bukkit.ChatColor;
	import org.bukkit.Material;
	import org.bukkit.World;
	import org.bukkit.block.Block;
	import org.bukkit.block.CreatureSpawner;
	import org.bukkit.block.Sign;
	import org.bukkit.configuration.file.FileConfiguration;
	import org.bukkit.entity.EntityType;
	import org.bukkit.entity.Player;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.Listener;
	import org.bukkit.event.block.Action;
	import org.bukkit.event.block.BlockBreakEvent;
	import org.bukkit.event.block.BlockPlaceEvent;
	import org.bukkit.event.block.SignChangeEvent;
	import org.bukkit.event.player.PlayerInteractEvent;
	import org.bukkit.inventory.ItemStack;
	import org.bukkit.inventory.PlayerInventory;
	import org.bukkit.inventory.meta.ItemMeta;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerChangeEvent;

	public class MainListener
	  implements Listener
	{
		@EventHandler
		public void onSpawnerChange(SilkSpawnersSpawnerChangeEvent event) {
			// Get information
			Player player = event.getPlayer();
			short entityID = event.getEntityID();
			CreatureSpawner spawner = event.getSpawner();
			Block block = event.getBlock();
			
			// Set new ID (pig = 90)
			event.setEntityID(90);
		}
	  @EventHandler
	  public void onBreak(BlockBreakEvent e)
	  {
	    ItemStack is = e.getPlayer().getItemInHand();
	    if ((is.hasItemMeta()) && (is.getItemMeta().hasDisplayName()) && 
	      (is.getItemMeta().getDisplayName().equals(ChatColor.RED + "Wrench")))
	      if (e.getBlock().getType().equals(Material.MOB_SPAWNER))
	      {
	        if (!e.getPlayer().hasPermission("wrench.use"))
	        {
	          String wrenchSetNoColour = Main.instance.getConfig().getString("NoPermsMsg").replaceAll("PLAYERNAME", e.getPlayer().getName());
	          String wrenchSet = ChatColor.translateAlternateColorCodes('&', wrenchSetNoColour);

	          e.getPlayer().sendMessage(wrenchSet);
	          e.setCancelled(true);
	          return;
	        }
	        ItemStack spawnerStack = new ItemStack(e.getBlock().getType());
	        ItemMeta im = spawnerStack.getItemMeta();
	        im.setDisplayName(((CreatureSpawner)e.getBlock().getState()).getCreatureTypeName().toUpperCase() + 
	          " SPAWNER");
	        spawnerStack.setItemMeta(im);
	        spawnerStack.setDurability(((CreatureSpawner)e.getBlock().getState()).getSpawnedType().getTypeId());
	        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), spawnerStack);
	        e.getPlayer().getInventory().setItemInHand(null);
	        e.getPlayer().updateInventory();
	      }
	      else
	      {
	        String wrenchblockSetNoColour = Main.instance.getConfig().getString("BlockBreakMsg").replaceAll("PLAYERNAME", e.getPlayer().getName());
	        String wrenchblockSet = ChatColor.translateAlternateColorCodes('&', wrenchblockSetNoColour);

	        e.getPlayer().sendMessage(wrenchblockSet);
	        e.setCancelled(true);
	      }
	  }

	  @EventHandler
	  public void onPlace(BlockPlaceEvent e)
	  {
	    if ((e.getBlockPlaced().getType().equals(Material.MOB_SPAWNER)) && 
	      (e.getItemInHand().hasItemMeta()) && 
	      (e.getItemInHand().getItemMeta().hasDisplayName()) && 
	      (EntityType.fromName(e.getItemInHand().getItemMeta().getDisplayName().replace(" SPAWNER", "")) != null))
	      ((CreatureSpawner)e.getBlockPlaced().getState()).setCreatureTypeByName(e.getItemInHand().getItemMeta()
	        .getDisplayName().replace(" Spawner", ""));
	  }

	  @EventHandler
	  public void onSign(SignChangeEvent e)
	  {
	    if (e.getLine(0).equalsIgnoreCase("[Wrench]"))
	    {
	      if (!e.getPlayer().hasPermission("wrench.sign"))
	      {
	        e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have permission to do this!");
	        e.getBlock().breakNaturally();
	        return;
	      }
	      e.setLine(0, ChatColor.RED + "Wrench");
	      try
	      {
	        e.setLine(1, ChatColor.GREEN + "Cost: " + Integer.parseInt(e.getLine(1)));
	      }
	      catch (NumberFormatException ex)
	      {
	        e.getBlock().breakNaturally();
	        e.getPlayer().sendMessage(
	          ChatColor.DARK_AQUA +"" + ChatColor.ITALIC + e.getLine(1) + ChatColor.RESET + 
	          ChatColor.DARK_RED + " is not an integer!");
	      }
	    }
	  }

	  @EventHandler
	  public void onInteract(PlayerInteractEvent e) {
	    if ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && ((e.getClickedBlock().getState() instanceof Sign)))
	    {
	      Sign sign = (Sign)e.getClickedBlock().getState();
	      if (sign.getLine(0).equals(ChatColor.RED + "Wrench"))
	      {
	        int cost = 0;
	        try
	        {
	          cost = Integer.parseInt(ChatColor.stripColor(sign.getLine(1).replace("Cost: ", "")));
	        }
	        catch (NumberFormatException ex)
	        {
	          e.getPlayer().sendMessage(ChatColor.DARK_RED + "This sign is not set up properly");
	          return;
	        }
	        if (e.getPlayer().getInventory().firstEmpty() != -1)
	        {
	          if (Main.economy.getBalance(e.getPlayer().getName()) >= cost)
	          {
	            e.getPlayer().getInventory().addItem(new ItemStack[] { Main.getWrench() });
	            e.getPlayer().updateInventory();
	            Main.economy.withdrawPlayer(e.getPlayer().getName(), cost);
	            String wrenchbuySetNoColour = Main.instance.getConfig().getString("WrenchBuyMsg").replaceAll("PLAYERNAME", e.getPlayer().getName());
	            String wrenchbuySet = ChatColor.translateAlternateColorCodes('&', wrenchbuySetNoColour);
	            e.getPlayer().sendMessage(wrenchbuySet);
	          }
	          else
	          {
	            String wrenchFailBuyMsgSetNoColour = Main.instance.getConfig().getString("WrenchFailBuyMsg").replaceAll("PLAYERNAME", e.getPlayer().getName());
	            String WrenchFailBuyMsg = ChatColor.translateAlternateColorCodes('&', wrenchFailBuyMsgSetNoColour);
	            e.getPlayer().sendMessage(WrenchFailBuyMsg);
	          }
	        }
	        else {
	          String wrenchfullinvSetNoColour = Main.instance.getConfig().getString("WrenchFullInvMsg").replaceAll("PLAYERNAME", e.getPlayer().getName());
	          String WrenchFullInvMsg = ChatColor.translateAlternateColorCodes('&', wrenchfullinvSetNoColour);
	          e.getPlayer().sendMessage(WrenchFullInvMsg);
	        }
	      }
	    }
	  }
	}

