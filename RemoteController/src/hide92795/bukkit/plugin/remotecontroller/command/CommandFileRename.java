package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import java.io.File;

public class CommandFileRename implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] data = arg.split(":");
				File old_file = new File(plugin.getRoot(), data[0].substring(1).replace('/', File.separatorChar));
				File new_file = new File(old_file.getParentFile(), data[1]);
				boolean success = old_file.renameTo(new_file);
				if (success) {
					plugin.getLogger().info(connection.getUser() + " has renamed directory/file" + arg);
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
