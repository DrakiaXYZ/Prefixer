package com.sparkedia.valrix.Prefixer;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.craftbukkit.TextWrapper;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Prefixer extends JavaPlugin {
	protected PrefixPlayerListener pListener;
	public Logger log;
	public Property prefix;
	public String pName = null;
	private Property config = null;
	public PermissionHandler permission = null;
	
	// Function to do the symbol replacement
	private String format(String str) {
		str = str.replace("&0", "\u00A70"); // Black
		str = str.replace("&1", "\u00A71"); // Dark Blue
		str = str.replace("&2", "\u00A72"); // Dark Green
		str = str.replace("&3", "\u00A73"); // Dark Aqua
		str = str.replace("&4", "\u00A74"); // Dark Red
		str = str.replace("&5", "\u00A75"); // Dark Purple
		str = str.replace("&6", "\u00A76"); // Gold
		str = str.replace("&7", "\u00A77"); // Gray
		str = str.replace("&8", "\u00A78"); // Dark Gray
		str = str.replace("&9", "\u00A79"); // Blue
		str = str.replace("&A", "\u00A7A"); // Green
		str = str.replace("&B", "\u00A7B"); // Aqua
		str = str.replace("&C", "\u00A7C"); // Red
		str = str.replace("&D", "\u00A7D"); // Light Purple
		str = str.replace("&E", "\u00A7E"); // Yellow
		str = str.replace("&F", "\u00A7F"); // White
		return str;
	}
	
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		log.info('['+pName+"] v"+pdf.getVersion()+" has been disabled.");
	}

	public void onEnable() {
		log = this.getServer().getLogger();
		
		PluginDescriptionFile pdf = this.getDescription();
		pName = pdf.getName();

		if (!(this.getDataFolder().isDirectory())) {
			this.getDataFolder().mkdir();
		}
		prefix = new Property(this.getDataFolder()+"/players.prefix", this);
		//Does the config exist, if not then make a new blank one
		if (!(new File(this.getDataFolder()+"/config.txt").exists())) {
			config = new Property(this.getDataFolder()+"/config.txt", this);
			config.setBoolean("OP", true); //OP only by default
			config.save();
		} else {
			config = new Property(this.getDataFolder()+"/config.txt", this);
		}

		// Set up Permissions support
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permission == null) {
			if (test != null) {
				this.permission = ((Permissions)test).getHandler();
			} else {
				log.info('['+pName+"]: Permission system not detected.");
			}
		}
		
		pListener = new PrefixPlayerListener(this);
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, pListener, Event.Priority.Normal, this);
		
		log.info('['+pName+"] v"+pdf.getVersion()+" has been enabled.");
		
		getCommand("prefix").setExecutor(new CommandExecutor() {
			public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
				String cmdName = cmd.getName();
				if (sender instanceof Player) {
					Player player = ((Player)sender);
					// Sender has permissions for /prefix or (sender is an OP or OP=false)
					if (permission != null) {
						if (permission != null) {
							if (args.length == 1) {
								if (args[0].equalsIgnoreCase("list") && permission.has(player, "prefixer.list")) {
									// Display a list of colors for the user
									player.sendMessage("Color List:");
									String color;
									String msg = "";
									String cols = "ABCDEF";
									for (int i = 0; i <= 15; i++) {
										color = ChatColor.getByCode(i).name();
										if (msg.length() == 0) {
											msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+"-&"+i;
											continue;
										}
										msg += (i < 10) ? " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+"-&"+i : " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+"-&"+cols.charAt(i-10);
										TextWrapper.wrapText(msg);
									}
									player.sendMessage(msg);
									return true;
								} else if ((hasPrefix(args[0]) && (permission.has(player, "prefixer.remove"))) || args[0].equalsIgnoreCase(player.getName().toLowerCase())) {
									// Only people with permission to remove another's prefix or the prefix owner can remove
									removePrefix(args[0]);
									return true;
								} else {
									if (permission.has(player, "prefixer.self")) {
										// Set a prefix for the user calling the command if they have permission
										setPrefix(player.getName(), args[0]);
										return true;
									}
									player.sendMessage("You don't have permission to set your prefix.");
									return true;
								}
							} else if (args.length == 2) {
								// /prefix <name> <prefix>
								if ((hasPrefix(args[0]) && (permission.has(player, "prefixer.other"))) || (args[0].equalsIgnoreCase(player.getName().toLowerCase()) && permission.has(player, "prefixer.self"))) {
									// Name exists. They have permission to set another's prefix or can set own.
									setPrefix(args[0], args[1]);
									return true;
								}
								return true;
							}
						}
					} else if (player.isOp() || !config.getBoolean("OP")) {
						// Permissions isn't enabled
						if (cmdName.equalsIgnoreCase("prefix")) {
							if (args.length == 1) {
								if (args[0].equalsIgnoreCase("list")) {
									// Display a list of colors for the user
									player.sendMessage("Color List:");
									String color;
									String msg = "";
									String cols = "ABCDEF";
									for (int i = 0; i <= 15; i++) {
										color = ChatColor.getByCode(i).name();
										if (msg.length() == 0) {
											msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+"-&"+i;
											continue;
										}
										msg += (i < 10) ? " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+"-&"+i : " "+ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+"-&"+cols.charAt(i-10);
										TextWrapper.wrapText(msg);
									}
									player.sendMessage(msg);
									return true;
								} else if (hasPrefix(args[0]) && (player.isOp() || !config.getBoolean("OP"))) {
									// Only people with permission to remove another's prefix or the prefix owner can remove
									if (args[0].equalsIgnoreCase(player.getName().toLowerCase())) {
										removePrefix(args[0]);
									}
									return true;
								} else if (!hasPrefix(args[0])) {
									// Don't let them set the prefix equal to their own name
									if (args[0].equalsIgnoreCase(player.getName().toLowerCase())) return true;
									// If not trying to remove a prefix they don't already have...
									if (player.isOp() || !config.getBoolean("OP")) {
										// Set a prefix for the user calling the command if they have permission
										setPrefix(player.getName(), args[0]);
										return true;
									}
									player.sendMessage("You don't have permission to set your prefix.");
									return true;
								}
							} else if (args.length == 2) {
								// /prefix <name> <prefix>
								if (hasPrefix(args[0]) && (player.isOp() || (!config.getBoolean("OP") && args[0].equalsIgnoreCase(player.getName().toLowerCase())))) {
									// Name exists. Are OP or is allowed to set own prefix
									setPrefix(args[0], args[1]);
									return true;
								}
								return true;
							}
						}
					}
				}
				return false;
			}
		});
	}
	
	public String getPrefix(String name) {
		name = name.toLowerCase();
		if (!prefix.isEmpty(name)) {
			return prefix.getString(name);
		}
		return "";
	}
	
	public boolean setPrefix(String name, String pref) {
		name = name.toLowerCase();
		if (pref.trim().length() > 0) {
			prefix.setString(name, format(pref+"&F"));
			prefix.save();
			return true;
		}
		return false;
	}
	
	public boolean removePrefix(String name) {
		name = name.toLowerCase();
		if (prefix.keyExists(name)) {
			prefix.remove(name);
			return true;
		}
		return false;
	}
	
	public boolean hasPrefix(String name) {
		name = name.toLowerCase();
		if (prefix.keyExists(name) && !prefix.isEmpty(name)) {
			return true;
		}
		return false;
	}
}
