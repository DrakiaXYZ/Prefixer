package com.sparkedia.valrix.Prefixer;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class PrefixPlayerListener extends PlayerListener {
	public Prefixer plugin;
	private Property prefix;
	
	public PrefixPlayerListener(Prefixer plugin) {
		this.plugin = plugin;
		this.prefix = plugin.prefix;
	}

	@Override
	public void onPlayerChat(PlayerChatEvent e) {
		String name = e.getPlayer().getName().toLowerCase();
		if (prefix.keyExists(name) && plugin.hasPrefix(name)) {
			e.setFormat(prefix.getString(name)+' '+e.getFormat());
		}
	}
}
