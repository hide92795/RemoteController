package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;

public class CommandNotificationUnreadCount implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				int count = plugin.getNotificationUnreadCount();
				connection.send("NOTIFICATION_UNREAD_COUNT", pid, Integer.toString(count));
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occurred in CommandNotificationUnreadCount!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return false;
	}
}
