package com.sparkedia.valrix.Prefixer;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class PrefixPlayerListener extends PlayerListener {
	public static Prefixer plugin;
	
	public PrefixPlayerListener(Prefixer instance) {
		plugin = instance;
	}
	
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		if (Prefixer.prefix.keyExists(name)) {
			String pre = Prefixer.prefix.getString(name);
			event.setFormat("["+pre+"] <%s> %s");
		}
	}
}
