package com.sparkedia.valrix.Prefixer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class PrefixPlayerListener extends PlayerListener {
	public Prefixer plugin;
	
	public PrefixPlayerListener(Prefixer plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		if (Prefixer.prefix.keyExists(name)) {
			String pre = Prefixer.prefix.getString(name);
			String[] split = pre.split("_");
			if (split.length > 1) {
				String color = split[1];
				for (int i = 0; i <= 15; i++) {
					String col = ChatColor.getByCode(i).name();
					if (color.compareToIgnoreCase(col.toLowerCase().replace("_", "")) < 2) {
						event.setFormat("["+ChatColor.valueOf(col)+split[0]+ChatColor.WHITE+"] <%s> %s");
						break;
					}
				}
			}
		}
	}
}
