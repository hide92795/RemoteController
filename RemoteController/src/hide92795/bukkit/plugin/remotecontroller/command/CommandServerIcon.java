package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;

public class CommandServerIcon implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				char[] data = plugin.config.server_icon;
				if (data == null) {
					connection.send("SERVER_ICON", pid, "");
				} else {
					connection.send("SERVER_ICON", pid, new String(data));
				}
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e1) {
			plugin.getLogger().severe("An error has occured in CommandServerInfo!");
			connection.send("ERROR", pid, "EXCEPTION:" + e1.getMessage());
			e1.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return false;
	}
}
