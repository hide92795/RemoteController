package hide92795.bukkit.plugin.remotecontroller.compatibility;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.command.Command;

public class CommandDispatcher {
	public void dispatch(Command command, RemoteController plugin, ClientConnection connection, int pid, String arg) {
		command.doCommand(plugin, connection, pid, arg);
	}
}
