package hide92795.bukkit.plugin.remotecontroller.compatibility;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.command.Command;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandDispatcherWithBukkitRunnable extends CommandDispatcher {
	@Override
	public void dispatch(final Command command, final RemoteController plugin, final ClientConnection connection, final int pid, final String arg) {
		if (command.mustRunOnMainThread()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					command.doCommand(plugin, connection, pid, arg);
				}
			}.runTaskLater(plugin, 0);
		} else {
			command.doCommand(plugin, connection, pid, arg);
		}
	}
}
