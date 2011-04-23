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
		if (prefix.keyExists(name)) {
			if (!prefix.isEmpty(name)) {
				String pre = prefix.getString(name);
				e.setFormat(pre+' '+e.getFormat());
			}
		}
	}
}
