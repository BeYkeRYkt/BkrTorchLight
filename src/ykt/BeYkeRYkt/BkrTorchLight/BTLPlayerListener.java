package ykt.BeYkeRYkt.BkrTorchLight;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ykt.BeYkeRYkt.BkrTorchLight.update.Updater;

public class BTLPlayerListener
implements Listener
{
public BTL plugin;
public Location toPlayerLocation;
public Location fromPlayerLocation;

public BTLPlayerListener(BTL instance)
{
  this.plugin = instance;
}

@EventHandler
public void onPlayerDeath(PlayerDeathEvent event) {
Player player = event.getEntity();
			plugin.deleteLightSource(player);
    }


@EventHandler
public void onPlayerTeleport(PlayerTeleportEvent event) {
Player player = event.getPlayer();
			plugin.deleteLightSource(player);
    }

@EventHandler
public void onPlayerChangeWorlds(PlayerChangedWorldEvent event) {
	Player player = event.getPlayer();
	plugin.deleteLightSource(player);
}

@EventHandler
public void onItemHeldChange(PlayerItemHeldEvent event)
{
  Player player = event.getPlayer();
	if (plugin.getConfig().getBoolean("Worlds." + event.getPlayer().getWorld().getName()) || event.getPlayer().isOp()) {
		if (this.plugin.isUsing.contains(event.getPlayer())) {
  {
    try
    {
		if (event.getPlayer().getInventory().getItem(event.getNewSlot()) != null) {
			if (plugin.isValid(event.getPlayer().getInventory().getItem(event.getNewSlot()).getTypeId())) {
        BTL.createLightSource(toPlayerLocation, player, plugin.getConfig().getInt("Light.lightRadius"));
      }
		}else{
        BTL.deleteLightSource(this.fromPlayerLocation, player);
    }
    }
    catch (NullPointerException localNullPointerException)
    {
    }
  }
}
	}}

@EventHandler
public void onEntityDamageByEntity (EntityDamageByEntityEvent event) {
	if ( event.getDamager() instanceof Player) {
		  Player player = (Player) event.getDamager();
		if (this.plugin.isUsing.contains(player)) {
			event.setCancelled(false);
		}
	}
	}


@EventHandler
public void onPlayerMove(PlayerMoveEvent event)
{
  Player player = event.getPlayer();
  String playerName = player.getName();
	if (plugin.getConfig().getBoolean("Worlds." + event.getPlayer().getWorld().getName()) || event.getPlayer().isOp()) {
		if (this.plugin.isUsing.contains(event.getPlayer())) {

	 for (Player p : Bukkit.getOnlinePlayers()){

  {
    this.fromPlayerLocation = event.getFrom();
    this.toPlayerLocation = event.getTo();
    
	if (plugin.isValid(event.getPlayer().getItemInHand().getTypeId())) {
    {
      if (!this.toPlayerLocation.getBlock().isLiquid())
      {
        if ((this.fromPlayerLocation.getBlockX() != this.toPlayerLocation
          .getBlockX()) || 
          (this.fromPlayerLocation.getBlockY() != this.toPlayerLocation
          .getBlockY()) || (this.fromPlayerLocation
          .getBlockZ() != this.toPlayerLocation.getBlockZ()))
        {
          BTL.deleteLightSource(this.fromPlayerLocation, player);
          BTL.createLightSource(this.toPlayerLocation, player, plugin.getConfig().getInt("Light.lightRadius"));
        }

      }else{
    {
      BTL.deleteLightSource(this.fromPlayerLocation, player);
    }
      }
    }
    }else{
     BTL.deleteLightSource(this.fromPlayerLocation, player);
  }
}
	 }
		}
}
}


@EventHandler
public void onPlayerQuit(PlayerQuitEvent event)
{
  Player player = event.getPlayer();
  String playerName = player.getName();
  BTL.deleteLightSource(player);
	plugin.isUsing.remove(event.getPlayer());
}

@EventHandler
public void onPlayerJoin(PlayerJoinEvent event)
{
  Player player = event.getPlayer();
  String playerName = player.getName();
if(plugin.getConfig().getBoolean("auto-update", true))
	  if(player.hasPermission("BTL.update") && BTL.update ||  player.isOp()) 
	  {
		player.sendMessage(ChatColor.GREEN +"======" + ChatColor.AQUA + "BkrTorchLight:UpdateSystem" + ChatColor.GREEN + ("======"));
	    player.sendMessage(ChatColor.GREEN +"An update is available: " + BTL.name + "(" + BTL.size + " bytes");
	    // Will look like - An update is available: AntiCheat v1.3.6 (93738 bytes)
	    player.sendMessage(ChatColor.RED + "Type /btl update if you would like to update.");
	  }
}


@EventHandler
public void onPlayerLogin(PlayerLoginEvent event)
{

  Player player = event.getPlayer();
  String playerName = player.getName();
	if (plugin.getConfig().getBoolean("Worlds." + event.getPlayer().getWorld().getName()) || event.getPlayer().isOp()) {
    
  }
}
}

