package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.dynmap.RemoteControllerDynmap;

public class CommandDynmap implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				if (plugin.config.enable_dynmap_feature) {
					if (plugin.config.dynmap_address == null || plugin.config.dynmap_address.equals("")) {
						int port = 0;
						if (plugin.getServer().getPluginManager().isPluginEnabled("dynmap")) {
							port = RemoteControllerDynmap.getPort(plugin);
						}
						connection.send("DYNMAP", pid, String.valueOf(port));
					} else {
						connection.send("DYNMAP", pid, plugin.config.dynmap_address);
					}
				} else {
					connection.send("DYNMAP", pid, String.valueOf(0));
				}
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occurred in CommandDynmap!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}
}
