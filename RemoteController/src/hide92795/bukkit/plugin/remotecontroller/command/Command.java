package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;

public interface Command {
	void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg);

	boolean mustRunOnMainThread();
}
