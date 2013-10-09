package hide92795.bukkit.plugin.remotecontroller.listener;

import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerBroadcastEvent;

public class BroadcastListener implements Listener {
	private final RemoteController plugin;

	public BroadcastListener(RemoteController plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBroadcast(ServerBroadcastEvent event) {
		plugin.onChatLogUpdate(Util.removeEscapeSequence(event.getMessage()));
	}
}
