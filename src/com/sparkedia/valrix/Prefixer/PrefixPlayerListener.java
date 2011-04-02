package com.sparkedia.valrix.Prefixer;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class PrefixPlayerListener extends PlayerListener {
	public Prefixer plugin;
	private Property prefix;
	
	public PrefixPlayerListener(Prefixer plugin) {
		this.plugin = plugin;
		this.prefix = plugin.prefix;
	}
	
	public void onPlayerChat(PlayerChatEvent e) {
		String name = e.getPlayer().getName().toLowerCase();
		if (!prefix.keyExists(name)) {
			prefix.setString(name, "");
		}
		if (prefix.keyExists(name) && prefix.hasValue(name)) {
			String pre = prefix.getString(name);
			String[] split = pre.split("_");
			if (split.length > 1) {
				String color = split[1];
				for (int i = 0; i <= 15; i++) {
					String col = ChatColor.getByCode(i).name();
					if (color.equalsIgnoreCase(col.toLowerCase().replace("_", ""))) {
						e.setFormat("["+ChatColor.valueOf(col)+split[0]+ChatColor.WHITE+"] "+e.getFormat());
						break;
					}
				}
			}
		}
	}
}
