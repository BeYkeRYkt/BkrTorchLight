package ykt.BeYkeRYkt.BkrTorchLight;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.server.v1_5_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.EnumSkyBlock;
import net.minecraft.server.v1_5_R3.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_5_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ykt.BeYkeRYkt.BkrTorchLight.mcstats.Metrics;
import ykt.BeYkeRYkt.BkrTorchLight.update.Updater;

public class BTL extends JavaPlugin
{
public static BTL plugin;
public static Logger logger = Logger.getLogger("Minecraft");
public String version;
  public final BTLPlayerListener playerListener = new BTLPlayerListener(
    this);
	public Set<Player> isUsing = new HashSet<Player>();
	public boolean enableOnLoad = false;
	public Set<Player> isHelmetUse = new HashSet<Player>();
	 public Updater updater;
	 public static boolean update = false;
	 public static String name = "";
	 public static long size = 0;
	public Updater updater2;
    private static int invulnerableTicks = 60;
    public static List chunkCoordIntPairQueue = new LinkedList();
    public static MinecraftServer server;
    public static String worldName;
	
	
    public void onDisable()
  {
    PluginDescriptionFile pdfFile = getDescription();

    getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + 
      " is now disabled.");
	 for (Player p : Bukkit.getOnlinePlayers()){
			deleteLightSource(p);
		  }
  }

@Override
public void onLoad() {
	// Obtain logger
	logger = getLogger();
}

public void onEnable()
  {
	try {
	    Metrics metrics = new Metrics(this);
	    metrics.start();
	} catch (IOException e) {
	    // Failed to submit the stats :-(
	}
    PluginDescriptionFile pdfFile = getDescription();
    getServer().getPluginManager().registerEvents(new BTLPlayerListener(this), this);
    getServer().getPluginManager().registerEvents(new BTLHelmet(this), this);
    getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + 
      " is now enabled.");
	PluginDescriptionFile pdFile = getDescription();
	try {
		FileConfiguration fc = getConfig();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			fc.options().header("BkrTorchLight v" + pdFile.getVersion() + " Configuration" + 
				"\nby BeYkeRYkt");
			fc.addDefault("auto-update", true);
			fc.addDefault("message-torch-enable", true);
			fc.addDefault("message-headlamp-enable", true);
			fc.addDefault("ItemIDs.slot1", 10);
			fc.addDefault("ItemIDs.slot2", 11);
			fc.addDefault("ItemIDs.slot3", 50);
			fc.addDefault("ItemIDs.slot4", 51);
			fc.addDefault("ItemIDs.slot5", 89);
			fc.addDefault("ItemIDs.slot6", 91);
			fc.addDefault("ItemIDs.slot7", 327);
			fc.addDefault("ItemIDs.slot8", 369);
			fc.addDefault("Strings.activate", "Torch enabled.");
			fc.addDefault("Strings.deactivate", "Torch disabled.");
			fc.addDefault("Strings.disabled", "The plugin is disabled in this world.");
			fc.addDefault("HeadLamp.activate", "HeadLamp enabled.");
			fc.addDefault("HeadLamp.deactivate", "HeadLamp disabled.");
			fc.addDefault("HeadLamp.disabled", "The plugin is disabled in this world.");
			fc.addDefault("Light.lightRadius", 15);
			fc.addDefault("HeadLight.lightRadius", 10);
			fc.addDefault("Helmets.slot1", 89);
			fc.addDefault("Helmets.slot2", 91);
			fc.addDefault("Helmets.slot3", 124);
			List<World> worlds = getServer().getWorlds();
			for (World world: worlds) {
				fc.addDefault("Worlds." + world.getName(), true);
			}
			fc.options().copyDefaults(true);
			saveConfig();
			fc.options().copyDefaults(false);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
  }
  


//New Chunk Update
	public static List<Chunk> getChunks(Player p) {

		List<Chunk> res = new ArrayList<Chunk>();
		double x = p.getLocation().getX();
		double y = p.getLocation().getY();
		double z = p.getLocation().getZ();
		Location loc = new Location(p.getWorld(), x, y, z);
		Location posx = new Location(p.getWorld(), x + 16, y, z);
		Location negx = new Location(p.getWorld(), x - 16, y, z);
		Location posz = new Location(p.getWorld(), x, y, z + 16);
		Location negz = new Location(p.getWorld(), x, y, z - 16);
		Location diag1 = new Location(p.getWorld(), x + 16, y, z + 16);
		Location diag2 = new Location(p.getWorld(), x - 16, y, z + 16);
		Location diag3 = new Location(p.getWorld(), x + 16, y, z - 16);
		Location diag4 = new Location(p.getWorld(), x - 16, y, z - 16);
		res.add((Chunk) loc.getChunk());
		res.add((Chunk) posx.getChunk());
		res.add((Chunk) negx.getChunk());
		res.add((Chunk) posz.getChunk());
		res.add((Chunk) negz.getChunk());
		res.add((Chunk) diag1.getChunk());
		res.add((Chunk) diag2.getChunk());
		res.add((Chunk) diag3.getChunk());
		res.add((Chunk) diag4.getChunk());
		return res;
	}
	
	public static void queueChunkForUpdate(Player player, int cx, int cz) {
		 for (Player p : Bukkit.getOnlinePlayers()){
			    CraftWorld cWorld = (CraftWorld)player.getWorld();
		for (Chunk c : getChunks(p)) {
			((CraftChunk)c).getHandle().initLighting();
			((CraftPlayer) player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(c.getX(), c.getZ()));
				}
		 }	
	}
  
  //Light
  public static void createLightSource(Location toPlayerLocation, Player player, int level)
  {
    CraftWorld cWorld = (CraftWorld)toPlayerLocation.getWorld();    
    int xNew = toPlayerLocation.getBlockX();
    int yNew = toPlayerLocation.getBlockY() + 2;
    int zNew = toPlayerLocation.getBlockZ();
    
    int lightLevel = level;
    
	cWorld.getHandle().b(EnumSkyBlock.BLOCK, xNew, yNew, zNew, lightLevel);
	
    Location newSource = new Location(cWorld, xNew, yNew - 1, zNew);
    Material blockMaterial = newSource.getBlock().getType();
    byte blockData = newSource.getBlock().getData();
    newSource.getBlock().setType(blockMaterial);
    newSource.getBlock().setData(blockData);
queueChunkForUpdate(player, xNew, zNew);
			}
  


  public static void createLightSource(Player player, int level)
  {
    CraftWorld cWorld = (CraftWorld)player.getWorld();
    Location playerLocation = player.getLocation().getBlock().getLocation();

    int xNew = playerLocation.getBlockX();
    int yNew = playerLocation.getBlockY() + 2;
    int zNew = playerLocation.getBlockZ();

    int lightLevel = level;

    cWorld.getHandle().b(EnumSkyBlock.BLOCK, xNew , yNew, zNew, lightLevel);

    Location newSource = new Location(cWorld, xNew, yNew + 1, zNew);
    Material blockMaterial = newSource.getBlock().getType();
    byte blockData = newSource.getBlock().getData();
    newSource.getBlock().setType(blockMaterial);
    newSource.getBlock().setData(blockData);
    queueChunkForUpdate(player, xNew, zNew);
  }

  public static void deleteLightSource(Location fromPlayerLocation, Player player)
  {
    CraftWorld cWorld = (CraftWorld)fromPlayerLocation.getWorld();

    int xPrevious = fromPlayerLocation.getBlockX();
    int yPrevious = fromPlayerLocation.getBlockY() + 2;
    int zPrevious = fromPlayerLocation.getBlockZ();

    Location previousSource = new Location(cWorld, xPrevious, yPrevious, zPrevious);
    Material blockMaterial = previousSource.getBlock().getType();
    byte blockData = previousSource.getBlock().getData();
    previousSource.getBlock().setType(blockMaterial);
    previousSource.getBlock().setData(blockData);

    queueChunkForUpdate(player, xPrevious, zPrevious);
  }


  public static void deleteLightSource(Player player)
  {
    CraftWorld cWorld = (CraftWorld)player.getWorld();
    Location playerLocation = player.getLocation().getBlock().getLocation();

    if (playerLocation != null)
    {
      int xPrevious = playerLocation.getBlockX();
      int yPrevious = playerLocation.getBlockY() + 2;
      int zPrevious = playerLocation.getBlockZ();

      Location previousSource = new Location(cWorld, xPrevious, yPrevious, zPrevious);
      Material blockMaterial = previousSource.getBlock().getType();
      byte blockData = previousSource.getBlock().getData();
      previousSource.getBlock().setType(blockMaterial);
      previousSource.getBlock().setData(blockData);
      queueChunkForUpdate(player, xPrevious, zPrevious);
    }
  }


	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player & (cmd.getName().equalsIgnoreCase("btl") || cmd.getName().equalsIgnoreCase("bkrtorchlight"))) {
			if(args.length == 0) {
				if (getConfig().getBoolean("Worlds." + ((Player)sender).getWorld().getName()) || ((Player)sender).getPlayer().isOp()) {
					if (!this.isUsing.contains(sender)) {
						this.isUsing.add((Player)sender);
						if (isValid(((Player)sender).getItemInHand().getTypeId())) {
						}
						if(this.getConfig().getBoolean("message-torch-enable", true)) {
						sender.sendMessage(ChatColor.GOLD + "[BkrTorchLight]" + ChatColor.GREEN + getConfig().getString("Strings.activate"));
						}else{
						}
					} else {
						if (isValid(((Player)sender).getItemInHand().getTypeId())){
							deleteLightSource((Player) sender);
						}
						this.isUsing.remove((Player)sender);
						if(this.getConfig().getBoolean("message-torch-enable", true)) {
						sender.sendMessage(ChatColor.GOLD + "[BkrTorchLight]" + ChatColor.RED + getConfig().getString("Strings.deactivate"));
					}else{
					}
					}
					return true;
				} else if (!getConfig().getBoolean("Worlds." + ((Player)sender).getWorld().getName())) {
					if(this.getConfig().getBoolean("message-torch-enable", true)) {
					sender.sendMessage(ChatColor.GOLD + "[BkrTorchLight]" + ChatColor.RED + getConfig().getString("Strings.disabled"));
					}else{
					}
					return true;
			}
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("i") || args[0].toLowerCase(Locale.ENGLISH).startsWith("info")) {
					PluginDescriptionFile pdFile = getDescription();
					sender.sendMessage(ChatColor.RED + "BkrTorchLight+ v" + pdFile.getVersion() + ChatColor.GRAY + " " + pdFile.getAuthors().toString());
					sender.sendMessage(pdFile.getDescription());
					return true;
				}
			}
			
			
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("h") || args[0].toLowerCase(Locale.ENGLISH).startsWith("helmet")) {
					if (getConfig().getBoolean("Worlds." + ((Player)sender).getWorld().getName()) || ((Player)sender).getPlayer().isOp()) {
						if (!this.isHelmetUse.contains(sender)) {
							this.isHelmetUse.add((Player)sender);
			if (isHelmet(((Player)sender).getInventory().getArmorContents()[3].getTypeId())) {
		}
			if(this.getConfig().getBoolean("message-headlamp-enable", true)) {
		sender.sendMessage(ChatColor.GOLD + "[BkrTorchLight]" + ChatColor.GREEN + getConfig().getString("HeadLamp.activate"));	
			}else{
			}
	} else {
			if (isHelmet(((Player)sender).getInventory().getArmorContents()[3].getTypeId())) {
			deleteLightSource((Player) sender);
		}
		this.isHelmetUse.remove((Player)sender);
		if(this.getConfig().getBoolean("message-headlamp-enable", true)) {
		sender.sendMessage(ChatColor.GOLD + "[BkrTorchLight]" + ChatColor.RED + getConfig().getString("HeadLamp.deactivate"));
		}else{
		}
	}
	return true;
} else if (!getConfig().getBoolean("Worlds." + ((Player)sender).getWorld().getName())) {
	if(this.getConfig().getBoolean("message-headlamp-enable", true)) {
	sender.sendMessage(ChatColor.GOLD + "[BkrTorchLight]" + ChatColor.RED + getConfig().getString("HeadLamp.disabled"));
	}else{
	}
					return true;
				}
			}
				if (args.length > 0) {
					  if(((Player)sender).hasPermission("BTL.update") && BTL.update ||  ((Player)sender).isOp()) {
					if (args[0].equalsIgnoreCase("up") || args[0].toLowerCase(Locale.ENGLISH).startsWith("update"))
						if(this.getConfig().getBoolean("auto-update", true)){
						updater2 = new Updater(this, "torchmobile", this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);  // Go straight to downloading, and announce progress to console.
						sender.sendMessage(ChatColor.GREEN +"======" + ChatColor.AQUA + "BkrTorchLight:UpdateSystem" + ChatColor.GREEN + ("======"));
					sender.sendMessage(ChatColor.GREEN + "Checking and Downloading. Check to Console...");	
					return true;
					}else{
						sender.sendMessage(ChatColor.GREEN +"======" + ChatColor.AQUA + "BkrTorchLight:UpdateSystem" + ChatColor.GREEN + ("======"));
						sender.sendMessage(ChatColor.RED + "'auto-update' is disabled. Check to Config");
					return true;
					  }else{
					  }
					  }else{
					sender.sendMessage(ChatColor.GREEN +"======" + ChatColor.AQUA + "BkrTorchLight:UpdateSystem" + ChatColor.GREEN + ("======"));
					sender.sendMessage(ChatColor.RED + "You do not have permission.");
					return true;
				}
			}
		}
		}
		return false;
	}
	public boolean isValid(int itemID) {
		if (itemID == getConfig().getInt("ItemIDs.slot1")) {
			return true;
		} else if (itemID == getConfig().getInt("ItemIDs.slot2")) {
			return true;
		} else if (itemID == getConfig().getInt("ItemIDs.slot3")) {
			return true;
		} else if (itemID == getConfig().getInt("ItemIDs.slot4")) {
			return true;
		} else if (itemID == getConfig().getInt("ItemIDs.slot5")) {
			return true;
		} else if (itemID == getConfig().getInt("ItemIDs.slot6")) {
			return true;
		} else if (itemID == getConfig().getInt("ItemIDs.slot7")) {
			return true;
		} else if (itemID == getConfig().getInt("ItemIDs.slot8")) {
			return true;
		}
		return false;
	}
	public boolean isHelmet(int helmetID) {
		if (helmetID == getConfig().getInt("Helmets.slot1")) {
			return true;
		} else if (helmetID == getConfig().getInt("Helmets.slot2")) {
			return true;
		} else if (helmetID == getConfig().getInt("Helmets.slot3")) {
			return true;
		}
		return false;
	}
}