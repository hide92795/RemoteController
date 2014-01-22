package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.org.apache.commons.lang3.StringUtils;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class CommandServerInfo implements Command {

	@Override
	public synchronized void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				String servername = plugin.getServer().getServerName().replaceAll("\r\n", "\n");
				String port = String.valueOf(plugin.getServer().getPort());
				String max = String.valueOf(plugin.getServer().getMaxPlayers());
				String current = String.valueOf(plugin.getServer().getOnlinePlayers().length);

				ArrayList<String> datas = new ArrayList<>();
				datas.add(new String(Base64Coder.encode(servername.getBytes(Charset.forName("UTF-8")))));
				datas.add(port);
				datas.add(max);
				datas.add(current);
				datas.addAll(plugin.getAdditionalInfo());

				connection.send("SERVER_INFO", pid, StringUtils.join(datas, ":"));
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e1) {
			plugin.getLogger().severe("An error has occured in CommandServerInfo!");
			connection.send("ERROR", pid, "EXCEPTION:" + e1.getMessage());
			e1.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}
}
