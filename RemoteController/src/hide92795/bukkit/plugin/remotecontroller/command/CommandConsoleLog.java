package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;

public class CommandConsoleLog implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				connection.setConsoleLogSendState(arg.equals("START"));
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occurred in CommandConsoleLog!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return false;
	}
}
