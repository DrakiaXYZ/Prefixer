package com.sparkedia.valrix.Prefixer;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Prefixer extends JavaPlugin {
	private final PrefixPlayerListener pListener = new PrefixPlayerListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static Property prefix;
	
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" has been disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.pListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" has been enabled.");
		
		if (!(new File("plugins/"+pdf.getName()).isDirectory())) {
			(new File("plugins/"+pdf.getName())).mkdir();
		}
		
		prefix = new Property("plugins/"+pdf.getName()+"/players.prefix");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		String cmdName = cmd.getName();
		if (sender instanceof Player) {
			if (((Player)sender).isOp()) {
				String name = args[0].toLowerCase();
				if (cmdName.equalsIgnoreCase("prefix")) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("list")) {
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
						} else if (Prefixer.prefix.keyExists(name)) {
							Prefixer.prefix.remove(name);
							return true;
						}
					} else if (args.length == 2) {
						String pre = args[1];
						prefix.setString(name, pre);
						return true;
					} else if(args.length == 3) {
						String pre = args[1];
						String color = args[2];
						prefix.setString(name, pre+"_"+color);
						return true;
					}
				}
			}
		}
		return false;
	}
}
