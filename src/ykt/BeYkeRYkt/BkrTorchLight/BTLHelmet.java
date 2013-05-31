package ykt.BeYkeRYkt.BkrTorchLight;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BTLHelmet implements Listener {

	private BTL plugin;
	public Location toPlayerLocation;
	public Location fromPlayerLocation;

    public BTLHelmet(BTL instance)
    {
      this.plugin = instance;
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BTL.deleteLightSource(player);
    	plugin.isHelmetUse.remove(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerMoveHeadLamp(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("Worlds." + event.getPlayer().getWorld().getName()) || event.getPlayer().isOp()) {
    		if (this.plugin.isHelmetUse.contains(event.getPlayer())) {
    			  {
    				    this.fromPlayerLocation = event.getFrom();
    				    this.toPlayerLocation = event.getTo();
    				    
				if (plugin.isHelmet(event.getPlayer().getInventory().getArmorContents()[3].getTypeId())) {
				    {
                BTL.deleteLightSource(this.fromPlayerLocation, player);
                BTL.createLightSource(this.toPlayerLocation, player, plugin.getConfig().getInt("HeadLight.lightRadius"));
		}
            }else{
            	{
            BTL.deleteLightSource(this.toPlayerLocation, player);
}
            }
    			  }
    		}
}
    }
}