package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandKick implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] args = arg.split(":", 2);
				Player player = Bukkit.getPlayerExact(args[0]);
				if (player != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("Kicked by an operator.");
					if (args.length != 1 && args[1].length() != 0) {
						sb.append("\n");
						sb.append("Reason: ");
						sb.append(args[1]);
					}
					player.kickPlayer(sb.toString());
					plugin.getLogger().info(connection.getUser() + " has kicked player :" + args[0]);
					connection.send("SUCCESS", pid, "");
				} else {
					connection.send("ERROR", pid, "NO_PLAYER");
				}
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occurred in CommandPlayers!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}
}
