package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.apache.commons.lang3.StringUtils;

public class CommandPlayers implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] names = plugin.getOnlinePlayerNames();
				String data;
				if (names.length == 0) {
					data = "";
				} else {
					data = StringUtils.join(plugin.getOnlinePlayerNames(), ":");
				}
				connection.send("PLAYERS", pid, data);
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occurred in CommandPlayers!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}

}
