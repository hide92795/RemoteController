package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CommandGamemode implements Command {
	@SuppressWarnings("deprecation")
	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] args = arg.split(":");
				String username = args[0];
				int gamemode = Integer.valueOf(args[1]);
				Player player = Bukkit.getPlayerExact(username);
				if (player == null) {
					connection.send("ERROR", pid, "NO_PLAYER");
					return;
				}

				player.setGameMode(GameMode.getByValue(gamemode));

				connection.send("SUCCESS", pid, "");
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e1) {
			plugin.getLogger().severe("An error has occurred in CommandPlayers!");
			connection.send("ERROR", pid, "EXCEPTION:" + e1.getMessage());
			e1.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}
}
