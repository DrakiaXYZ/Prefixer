package com.sparkedia.valrix.Prefixer;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Prefixer extends JavaPlugin {
	private final PrefixPlayerListener pListener = new PrefixPlayerListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static Property prefix = null;
	public String pName = null;
	private Property config = null;
	
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pName+"] v"+pdf.getVersion()+" has been disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.pListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.pListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdf = this.getDescription();
		pName = pdf.getName();
		log.info("["+pName+"] v"+pdf.getVersion()+" has been enabled.");
		
		if (!(this.getDataFolder().isDirectory())) {
			this.getDataFolder().mkdir();
		}
		if (prefix == null) {
			prefix = new Property(this.getDataFolder()+"/players.prefix", this);
		}
		if (config == null) {
			//Does the config exist, if not then make a new blank one
			if (!(new File(this.getDataFolder()+"/config.txt").exists())) {
				config = new Property(this.getDataFolder()+"/config.txt", this);
				config.setBoolean("OP", true); //OP only by default
			} else {
				config = new Property(this.getDataFolder()+"/config.txt", this);
			}
		}
		
		getCommand("prefix").setExecutor(new CommandExecutor() {
			public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
				String cmdName = cmd.getName();
				if (sender instanceof Player) {
					if (((Player)sender).isOp() || !config.getBoolean("OP")) { //only OP unless OP=false
						if (cmdName.equalsIgnoreCase("prefix")) {
							if (args.length == 1) {
								if (args[0].equalsIgnoreCase("list")) {
									//Display a list of colors for the user
									((Player)sender).sendMessage("Color List:");
									String color;
									String msg1 = "";
									String msg2 = "";
									for (int i = 0; i <= 15; i++) {
										color = ChatColor.getByCode(i).name();
										if (i == 0) {
											msg1 = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
										} else if (i > 0 && i < 7) {
											msg1 += " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
										} else if (i == 7) {
											msg2 = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
										} else {
											msg2 += " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "");
										}
									}
									((Player)sender).sendMessage(msg1);
									((Player)sender).sendMessage(msg2);
									return true;
								} else if ((Prefixer.prefix.keyExists(args[0].toLowerCase()) && ((Player)sender).isOp()) || (Prefixer.prefix.keyExists(args[0].toLowerCase()) && (args[0].equalsIgnoreCase(((Player)sender).getName())))) {
									// Only OP can remove prefixes, unless name provided matches sender's name
									Prefixer.prefix.remove(args[0].toLowerCase());
									return true;
								} else {
									//just a prefix for the user calling the command
									Prefixer.prefix.setString(((Player)sender).getName().toLowerCase(), args[0].toLowerCase());
									return true;
								}
							} else if (args.length == 2) {
								// /prefix <name> <prefix>
								if ((prefix.keyExists(args[0].toLowerCase()) && ((Player)sender).isOp()) || (prefix.keyExists(args[0].toLowerCase()) && args[0].toLowerCase().equalsIgnoreCase(((Player)sender).getName().toLowerCase()))) {
									String name = args[0].toLowerCase();
									String pre = args[1];
									prefix.setString(name, pre);
									return true;
								} else { // /prefix <prefix> <color>
									//perform check to make sure we get the right color
									String pre = args[0];
									String col = args[1].toLowerCase();
									String color;
									for (int i = 0; i <= 15; i++) {
										color = ChatColor.getByCode(i).name().toLowerCase().replace("_", "");
										if (col.equalsIgnoreCase(color)) {
											// chose prefix and color, set both to sender's name
											prefix.setString(((Player)sender).getName().toLowerCase(), pre+"_"+col);
											return true;
										}
									}
								}
								((Player)sender).sendMessage(ChatColor.RED+"Could not find that color.");
								return true;
							} else if(args.length == 3) {
								// /prefix <name> <prefix> <color>
								String name = args[0].toLowerCase();
								if ((prefix.keyExists(name) && ((Player)sender).isOp()) || (prefix.keyExists(name) && name.equalsIgnoreCase(((Player)sender).getName().toLowerCase()))) {
									String pre = args[1];
									String col = args[2].toLowerCase();
									String color;
									for (int i = 0; i <= 15; i++) {
										color = ChatColor.getByCode(i).name().toLowerCase().replace("_", "");
										if (col.equalsIgnoreCase(color)) {
											// chose name prefix and color, set both to sender's name
											prefix.setString(name, pre+"_"+col);
											return true;
										}
									}
									((Player)sender).sendMessage(ChatColor.RED+"Could not find that color.");
									return true;
								}
							}
						}
					}
				}
				return false;
			}
		});
	}
}
