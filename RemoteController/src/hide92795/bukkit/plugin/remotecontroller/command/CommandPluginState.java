package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CommandPluginState implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] datas = arg.split(":", 2);
				PluginManager manager = plugin.getServer().getPluginManager();
				Plugin plugin_ = manager.getPlugin(datas[1]);
				boolean changeState = Boolean.parseBoolean(datas[0]);
				if (plugin_ == null) {
					connection.send("ERROR", pid, "NO_PLUGIN");
				} else {
					if (changeState) {
						if (plugin_.isEnabled()) {
							connection.send("ERROR", pid, "PLUGIN_ALREADY_ENABLED");
						} else {
							manager.enablePlugin(plugin_);
							connection.send("SUCCESS", pid, "");
						}
					} else {
						if (!plugin_.isEnabled()) {
							connection.send("ERROR", pid, "PLUGIN_ALREADY_DISABLED");
						} else {
							manager.disablePlugin(plugin_);
							connection.send("SUCCESS", pid, "");
						}
					}
				}
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in CommandPluginState!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}

}
