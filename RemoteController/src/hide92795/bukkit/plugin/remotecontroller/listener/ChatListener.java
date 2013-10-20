package hide92795.bukkit.plugin.remotecontroller.listener;

import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	private final RemoteController plugin;

	public ChatListener(RemoteController plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBroadcast(AsyncPlayerChatEvent event) {
		String chat = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
		plugin.onChatLogUpdate(Util.convertColorCode(chat));
	}
}
