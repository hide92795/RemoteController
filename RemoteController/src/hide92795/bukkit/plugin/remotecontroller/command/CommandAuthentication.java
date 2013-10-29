package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;

public class CommandAuthentication implements Command {

	@Override
	public synchronized void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			String[] datas = arg.split(":");
			boolean auth = plugin.checkUser(datas[0], datas[1]);
			if (auth) {
				plugin.getLogger().info("User \"" + datas[0] + "\" has logged in.");
				connection.authorize(datas[0]);
				connection.send("AUTH", 0, "OK:" + plugin.getVersion() + ":" + plugin.getMinecraftVersion());
				connection.sendCharset();
			} else {
				connection.send("AUTH", 0, "ERROR");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in authentication!");
			connection.send("ERROR", 0, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return false;
	}
}
