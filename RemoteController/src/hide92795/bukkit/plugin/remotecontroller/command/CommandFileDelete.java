package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class CommandFileDelete implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				File file = new File(plugin.getRoot(), arg.substring(1).replace('/', File.separatorChar));
				boolean success;
				if (file.isDirectory()) {
					// Directory
					success = FileUtils.deleteQuietly(file);
				} else {
					// File
					success = file.delete();
				}
				if (success) {
					plugin.getLogger().info(connection.getUser() + " has deleted directory/file:" + arg);
					connection.send("SUCCESS", pid, "");
				} else {
					connection.send("ERROR", pid, "FAILED");
				}
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in CommandFileOpen!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return false;
	}
}