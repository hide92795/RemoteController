package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.util.FileComparator;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class CommandDirectory implements Command {

	@Override
	public void doCommand(final RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String dir_s = arg.replace('/', File.separatorChar);
				File dir;
				if (dir_s.length() == 0) {
					dir = plugin.getRoot();
				} else {
					dir = new File(plugin.getRoot(), dir_s);
				}
				File[] files = dir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						if (file.isDirectory()) {
							return true;
						}
						String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
						if (plugin.config.editable_extension.contains(extension)) {
							return true;
						}
						return false;
					}
				});
				Arrays.sort(files, new FileComparator());
				String[] files_s = Util.toFileStringArray(files);
				connection.send("DIRECTORY", pid, arg + ":" + StringUtils.join(files_s, ":"));
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
