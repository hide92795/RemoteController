package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import java.io.File;

public class CommandMk implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String path = arg.substring(1);
				File file = new File(plugin.getRoot(), path.replace('/', File.separatorChar));
				boolean success;
				if (path.endsWith("/")) {
					// Directory
					success = file.mkdir();
				} else {
					// File
					success = file.createNewFile();
				}
				if (success) {
					plugin.getLogger().info(connection.getUser() + " has created directory/file :" + arg);
					connection.send("SUCCESS", pid, "");
				} else {
					connection.send("ERROR", pid, "FAILED");
				}

			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in CommandDirectory!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return false;
	}
}
