package com.sparkedia.valrix.Prefixer;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;

public class Prefixer extends JavaPlugin {
	private final PrefixPlayerListener pListener = new PrefixPlayerListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static Property prefix;
	
	public Prefixer(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" has been disabled.");
	}

	@Override
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
				if (args.length == 2) {
					if (cmdName.equalsIgnoreCase("prefix")) {
						String name = args[0];
						String pre = args[1];
						prefix.setString(name, pre);
						return true;
					}
				}
			}
		}
		return false;
	}
}
