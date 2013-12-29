package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class CommandFileOpen implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] file_data = arg.split(":");
				File file = new File(plugin.getRoot(), file_data[0].substring(1).replace('/', File.separatorChar));
				if (Util.canRead(plugin.config.file_access, file)) {
					String charset = file_data[1];
					StringBuilder sb = new StringBuilder();
					try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(charset)))) {
						String str = br.readLine();
						if (str != null) {
							for (;;) {
								sb.append(str);
								str = br.readLine();
								if (str == null) {
									break;
								}
								sb.append("\n");
							}
						}

					} catch (Exception e) {
						throw e;
					}
					sb.insert(0, ":");
					sb.insert(0, charset);
					sb.insert(0, ":");
					sb.insert(0, file_data[0]);
					connection.send("FILE_OPEN", pid, sb.toString());
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
