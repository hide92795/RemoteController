package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.Type;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import org.bukkit.Bukkit;

public class CommandChat implements Command {
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String message = String.format(plugin.localize.getString(Type.CHAT_PREFIX), connection.getUser()) + arg;
				if (!plugin.isChatTypeBroadcast()) {
					plugin.onChatLogUpdate(Util.removeEscapeSequence(message));
				}
				Bukkit.broadcastMessage(message);
				connection.send("SUCCESS", pid, "");
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in CommandConsoleCommand!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}
}
