package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandBan implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] args = arg.split(":", 2);
				Bukkit.getOfflinePlayer(args[0]).setBanned(true);
				Player player = Bukkit.getPlayerExact(args[0]);
				plugin.getLogger().info(connection.getUser() + " has baned player :" + args[0]);
				if (player != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("Banned by admin.");
					if (args.length != 1 && args[1].length() != 0) {
						sb.append("\n");
						sb.append("Reason: ");
						sb.append(args[1]);
					}
					player.kickPlayer(sb.toString());
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
