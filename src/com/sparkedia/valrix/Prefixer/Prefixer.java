package com.sparkedia.valrix.Prefixer;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.craftbukkit.TextWrapper;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Prefixer extends JavaPlugin {
	protected PrefixPlayerListener pListener;
	public Logger log;
	public Property prefix;
	public File df;
	public String pName;
	public String version;
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
	
	// Function to reformat old prefixes to the new format
	private String reformat(String str) {
		// Switch statement is fast, but doesn't allow strings. Check the hashcode instead
		switch (str.substring(str.indexOf('_')+1).hashCode()) {
		case 93818879: // Black
			str = "\u00A70"+str.replace("_black", "");
			break;
		case 1741452496: // Dark Blue
			str = "\u00A71"+str.replace("_darkblue", "");
			break;
		case -1844766387: // Dark Green
			str = "\u00A72"+str.replace("_darkgreen", "");
			break;
		case 1741427506: // Dark Aqua
			str = "\u00A73"+str.replace("_darkaqua", "");
			break;
		case 1441664347: // Dark Red
			str = "\u00A74"+str.replace("_darkred", "");
			break;
		case -1092352334: // Dark Purple
			str = "\u00A75"+str.replace("_darkpurple", "");
			break;
		case 3178592: // Gold
			str = "\u00A76"+str.replace("_gold", "");
			break;
		case 3181155: // Gray
			str = "\u00A77"+str.replace("_gray", "");
			break;
		case 1741606617: // Dark Gray
			str = "\u00A78"+str.replace("_darkgray", "");
			break;
		case 3027034: // Blue
			str = "\u00A79"+str.replace("_blue", "");
			break;
		case 98619139: // Green
			str = "\u00A7A"+str.replace("_green", "");
			break;
		case 3002044: // Aqua
			str = "\u00A7B"+str.replace("_aqua", "");
			break;
		case 112785: // Red
			str = "\u00A7C"+str.replace("_red", "");
			break;
		case -1682598830: // Light Purple
			str = "\u00A7D"+str.replace("_lightpurple", "");
			break;
		case -734239628: // Yellow
			str = "\u00A7E"+str.replace("_yellow", "");
			break;
		case 113101865: // White
			str = "\u00A7F"+str.replace("_white", "");
			break;
		default:
			break;
		}
		return str;
	}
	
	@Override
	public void onDisable() {
		log.info('['+pName+"] has been disabled.");
	}

	@Override
	public void onEnable() {
		log = getServer().getLogger();
		
		pName = getDescription().getName();
		version = getDescription().getVersion();
		df = getDataFolder();

		if (!(df.isDirectory())) df.mkdir();
		
		prefix = new Property(df+"/players.prefix", "prefix", this);
		// Check if they have the updated prefix property file, otherwise update it to new format
		if (!version.equalsIgnoreCase(prefix.getString(pName+"Version"))) {
			LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
			prefix.remove(pName+"Version");
			prefix.remove(pName+"Type");
			for (String key : prefix.getKeys()) {
				// Reformat each player
				tmp.put(key, reformat(prefix.getString(key)));
			}
			prefix.rebuild(tmp);
		}
		
		//Does the config exist, if not then make a new blank one
		if (!(new File(df+"/config.txt").exists())) {
			config = new Property(df+"/config.txt", "config", this);
			config.setBoolean("OP", true); //OP only by default
			config.save();
		} else {
			config = new Property(df+"/config.txt", "config", this);
		}

		// Set up Permissions support
		if (getServer().getPluginManager().getPlugin("Permissions") != null) {
			this.permission = ((Permissions)getServer().getPluginManager().getPlugin("Permissions")).getHandler();
		} else {
			log.info('['+pName+"]: Permission system not detected. Defaulting to OP permissions.");
		}

		pListener = new PrefixPlayerListener(this);
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, pListener, Event.Priority.Normal, this);
		
		log.info('['+pName+"] v"+version+" has been enabled.");
		
		getCommand("prefix").setExecutor(new CommandExecutor() {
			public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
				if (sender instanceof Player) {
					Player player = ((Player)sender);
					// Sender has permissions for /prefix or (sender is an OP or OP=false)
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
							if (permission.has(player, "prefixer.other") || (args[0].equalsIgnoreCase(player.getName().toLowerCase()) && permission.has(player, "prefixer.self"))) {
								// Permission to set another's prefix *or* setting own name and has permission to
								setPrefix(args[0], args[1]);
								return true;
							}
							// Don't have permission to set another's prefix *or* have permission to set own *and* name isn't their's
							if (!permission.has(player, "prefixer.other") || permission.has(player, "prefixer.self") && !args[0].equalsIgnoreCase(player.getName().toLowerCase())) {
								player.sendMessage("You don't have permission to set someone else's prefix.");
								return true;
							}
							// Don't have permission to set another's prefix *and* no permission to set own prefix
							if (!permission.has(player, "prefix.other") && !permission.has(player, "prefixer.self") && args[0].equalsIgnoreCase(player.getName().toLowerCase())) {
								player.sendMessage("You don't have permission to set your prefix.");
								return true;
							}
							return true;
						}
					} else if (player.isOp() || !config.getBoolean("OP")) {
						// Permissions isn't enabled
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
							} else if (hasPrefix(args[0]) && (player.isOp() || (!config.getBoolean("OP") && args[0].equalsIgnoreCase(player.getName().toLowerCase())))) {
								// Only people with permission to remove another's prefix or the prefix owner can remove
								removePrefix(args[0]);
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
							if (player.isOp() || (!config.getBoolean("OP") && args[0].equalsIgnoreCase(player.getName().toLowerCase()))) {
								// sender is OP *or* not OP and setting own name
								setPrefix(args[0], args[1]);
								return true;
							}
							return true;
						}
					}
				} else if (sender instanceof ConsoleCommandSender) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("list")) {
							// Display a list of colors for the user
							sender.sendMessage("Color List:");
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
							sender.sendMessage(msg);
							return true;
						} else if (hasPrefix(args[0])) {
							// Console is removing a player's prefix
							removePrefix(args[0]);
							sender.sendMessage('['+pName+"]: Removed "+args[0]+"\'s prefix.");
							return true;
						}
					} else if (args.length == 2) {
						// Console is setting a player's prefix
						setPrefix(args[0], args[1]);
						sender.sendMessage('['+pName+"]: Gave "+args[0]+" the prefix "+format(args[1]));
						return true;
					}
				}
				return false;
			}
		});
	}
	
	public String getPrefix(String name) {
		return (!prefix.isEmpty(name.toLowerCase())) ? prefix.getString(name.toLowerCase()) : "";
	}
	
	public boolean setPrefix(String name, String pref) {
		if (pref.trim().length() > 0) {
			prefix.setString(name.toLowerCase(), format(pref+"&F"));
			prefix.save();
			return true;
		}
		return false;
	}
	
	public boolean removePrefix(String name) {
		if (prefix.keyExists(name.toLowerCase())) {
			prefix.setString(name.toLowerCase(), "");
			prefix.save();
			return true;
		}
		return false;
	}
	
	public boolean hasPrefix(String name) {
		return (prefix.getString(name.toLowerCase()).length()>0) ? true : false;
	}
}
