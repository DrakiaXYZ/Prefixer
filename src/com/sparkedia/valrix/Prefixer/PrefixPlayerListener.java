package com.sparkedia.valrix.Prefixer;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class PrefixPlayerListener extends PlayerListener {
	public Prefixer plugin;
	
	public PrefixPlayerListener(Prefixer plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerJoin(PlayerEvent event) {
		String name = event.getPlayer().getName().toLowerCase();
		if (!Prefixer.prefix.keyExists(name)) {
			Prefixer.prefix.setString(name, "");
		}
	}
	
	public void onPlayerChat(PlayerChatEvent event) {
		String name = event.getPlayer().getName().toLowerCase();
		if (Prefixer.prefix.keyExists(name) && Prefixer.prefix.hasValue(name)) {
			String pre = Prefixer.prefix.getString(name);
			String[] split = pre.split("_");
			if (split.length > 1) {
				String color = split[1];
				for (int i = 0; i <= 15; i++) {
					String col = ChatColor.getByCode(i).name();
					if (color.equalsIgnoreCase(col.toLowerCase().replace("_", ""))) {
						event.setFormat("["+ChatColor.valueOf(col)+split[0]+ChatColor.WHITE+"] "+event.getFormat());
						break;
					}
				}
			}
		}
	}
}
