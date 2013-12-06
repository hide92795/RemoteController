package hide92795.bukkit.plugin.remotecontroller.command;

import hide92795.bukkit.plugin.remotecontroller.ClientConnection;
import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.org.apache.commons.lang3.StringUtils;
import java.nio.charset.Charset;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class CommandPluginList implements Command {

	@Override
	public void doCommand(RemoteController plugin, ClientConnection connection, int pid, String arg) {
		try {
			if (connection.isAuthorized()) {
				Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
				String[] datas = new String[plugins.length];
				for (int i = 0; i < plugins.length; i++) {
					Plugin plugin_ = plugins[i];
					String plugin_data = plugin_.getName() + ":" + Boolean.toString(plugin_.isEnabled());
					datas[i] = String.valueOf(Base64Coder.encode(plugin_data.getBytes(Charset.forName("UTF-8"))));
				}
				connection.send("PLUGIN_LIST", pid, StringUtils.join(datas, ":"));
			} else {
				connection.send("ERROR", pid, "NOT_AUTH");
			}
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in CommandPluginList!");
			connection.send("ERROR", pid, "EXCEPTION:" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean mustRunOnMainThread() {
		return true;
	}

}
