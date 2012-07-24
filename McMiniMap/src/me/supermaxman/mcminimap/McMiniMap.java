package me.supermaxman.mcminimap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.map.CraftMapCanvas;
import org.bukkit.craftbukkit.map.CraftMapRenderer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Egg;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class McMiniMap extends JavaPlugin implements Listener{
	
	//Required
	public static McMiniMap plugin;
	public static Permission permission = null;
    
    private boolean setupPermissions(){
       RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
       if (permissionProvider != null) {
           permission = permissionProvider.getProvider();
       }
       return (permission != null);
   }
	public final Logger logger = Logger.getLogger("Minecraft");
	public static HashMap<Player, Boolean> miniMapToggle = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> miniMapZoom = new HashMap<Player, Integer>();
	
	
	@Override
	public void onDisable() {this.logger.info("McMiniMap Disabled.");}
	@Override
	public void onEnable() {
		setupPermissions();
        getServer().getPluginManager().registerEvents(new McMiniMap(), this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		McMiniMap.plugin = this;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
		   if(command.getName().equalsIgnoreCase("minimaptoggle")||command.getName().equalsIgnoreCase("mmt") ){
				if((permission.has((Player)sender,"mcminimap.toggle"))){
			   Player p = (Player) sender;
			   if(!miniMapToggle.containsKey(p)){
				   miniMapToggle.put(p, true);
				   p.sendMessage(ChatColor.AQUA+"MiniMap Enabled.");
				   
			   }else{
				   if(miniMapToggle.get(p)){
					   miniMapToggle.put(p, false);
					   p.sendMessage(ChatColor.AQUA+"MiniMap Disabled.");
					   
				   }else{
					   miniMapToggle.put(p, true);
					   p.sendMessage(ChatColor.AQUA+"MiniMap Enabled.");
					   
				   }
			   }
				}
		   }else if(command.getName().equalsIgnoreCase("minimapzoomin") ){
				if((permission.has((Player)sender,"mcminimap.zoom"))){

			   Player p = (Player) sender;
			   if(miniMapZoom.containsKey(p)){
				   if(miniMapZoom.get(p)>1){
					   miniMapZoom.put(p, miniMapZoom.get(p)-1);
				   }else{
					   miniMapZoom.put(p, 1);
				   }
			   }else{
				   miniMapZoom.put(p, 1);
			   }
			   p.sendMessage(ChatColor.AQUA+"MiniMap Zoom at "+(miniMapZoom.get(p))+".");
			}
		   }else if(command.getName().equalsIgnoreCase("minimapzoomout") ){
				if((permission.has((Player)sender,"mcminimap.zoom"))){
			   Player p = (Player) sender;
			   if(miniMapZoom.containsKey(p)){
				   if(miniMapZoom.get(p)<5){
					   miniMapZoom.put(p, miniMapZoom.get(p)+1);
				   }else{
					   miniMapZoom.put(p, 5);
				   }
			   }else{
				   miniMapZoom.put(p, 2);
			   }
			   p.sendMessage(ChatColor.AQUA+"MiniMap Zoom at "+(miniMapZoom.get(p))+".");
		   }
		   }
		}
		
		return true;
		
	}
	
	
	
	//Events
	
	
	
	
	
	
    @EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event){
		final Player player = event.getPlayer();
		if((permission.has(player,"mcminimap.zoom"))){
		if(player.getItemInHand().getType()==Material.MAP&&(miniMapToggle.containsKey(player))){
			if(miniMapToggle.get(player)){
			if(player.getLocation().getPitch()>=40){
				if (event.getAction()==Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK){
					//zoom out
					   if(miniMapZoom.containsKey(player)){
						   if(miniMapZoom.get(player)<5){
							   miniMapZoom.put(player, miniMapZoom.get(player)+1);
						   }else{
							   miniMapZoom.put(player, 5);
						   }
					   }else{
						   miniMapZoom.put(player, 2);
					   }
					   player.sendMessage(ChatColor.AQUA+"MiniMap Zoom at "+(miniMapZoom.get(player))+".");
					   event.setCancelled(true);
				}else if (event.getAction()==Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
					//zoom in
					   if(miniMapZoom.containsKey(player)){
						   if(miniMapZoom.get(player)>1){
							   miniMapZoom.put(player, miniMapZoom.get(player)-1);
						   }else{
							   miniMapZoom.put(player, 1);
						   }
					   }else{
						   miniMapZoom.put(player, 1);
					   }
					   player.sendMessage(ChatColor.AQUA+"MiniMap Zoom at "+(miniMapZoom.get(player))+".");
					   event.setCancelled(true);
				}
			}
		
				
				
			}
		}
		
		}
		
    }
   
    @EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if((permission.has(player,"mcminimap.toggle"))){
		if((player.getItemInHand().getType()==Material.MAP)&&(miniMapToggle.containsKey(player))){
			if(miniMapToggle.get(player)){
		MapView view = plugin.getServer().getMap(player.getItemInHand().getDurability());
		view.setWorld(player.getWorld());
		view.setCenterX(player.getLocation().getBlockX());
		view.setCenterZ(player.getLocation().getBlockZ());
		
		if(miniMapZoom.containsKey(player)){
			if(miniMapZoom.get(player)==1){
				view.setScale(Scale.CLOSEST);
			}else if(miniMapZoom.get(player)==2){
				view.setScale(Scale.CLOSE);
			}else if(miniMapZoom.get(player)==3){
				view.setScale(Scale.NORMAL);
			}else if(miniMapZoom.get(player)==4){
				view.setScale(Scale.FAR);
			}else if(miniMapZoom.get(player)==5){
				view.setScale(Scale.FARTHEST);
			}else{
				view.setScale(Scale.CLOSEST);
			}
		}else{
			view.setScale(Scale.CLOSEST);
		}
		player.sendMap(view);

		}
		}
    }
    }
	
	
	
	
}