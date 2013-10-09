package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGive implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String[] args = arg.split(":", 3);
				String playername = args[0];
				int num = Integer.parseInt(args[1]);
				String[] itemdata = args[2].split(":");

				Player player = Bukkit.getPlayerExact(playername);
				if (player != null) {
					Material material = Material.matchMaterial(itemdata[0]);
					if (material != null) {
						short data = 0;
						if (itemdata.length != 1) {
							data = Short.parseShort(itemdata[1]);
						}
						player.getInventory().addItem(new ItemStack(material, num, data));
						connection.send("SUCCESS", pid, "");
					} else {
						connection.send("ERROR", pid, "NO_ITEM");
					}
				} else {
					connection.send("ERROR", pid, "NO_PLAYER");
				}
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occurred in CommandGive!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}

	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}

}
