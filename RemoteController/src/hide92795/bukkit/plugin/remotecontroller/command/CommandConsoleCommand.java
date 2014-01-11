package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.bukkit.Bukkit;

public class CommandConsoleCommand implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				if (arg.startsWith("rcu") || arg.startsWith("remotecontroller-user")) {
					if (plugin.config.console_only) {
						plugin.getLogger().info("This command can only be accessed from console.");
						return;
					}
				}
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), arg);
				plugin.getLogger().info(connection.getUser() + " issued server command: " + arg);
				connection.send("SUCCESS", pid, "");
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in CommandConsoleCommand!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}
}
