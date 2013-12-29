package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class CommandFileEdit implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] file_data = arg.split(":", 3);
				File file = new File(plugin.getRoot(), file_data[0].substring(1).replace('/', File.separatorChar));
				if (Util.canWrite(plugin.config.file_access, file)) {
					String charset = file_data[1];
					try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), Charset.forName(charset));) {
						osw.append(file_data[2]);
						osw.flush();
					} catch (Exception e) {
						throw e;
					}
					plugin.getLogger().info(connection.getUser() + " has edited :" + file_data[0]);
					connection.send("SUCCESS", pid, "");
				} else {
					connection.send("ERROR", pid, "ACCESS_DENIED");
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
