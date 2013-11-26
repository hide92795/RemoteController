package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.MinecraftVersion;
import hide92795.android.remotecontroller.receivedata.AuthorizedData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.LogUtil;

public class CommandAuthentication implements Command {
	@Override
	public ReceiveData doCommand(final Connection connection, int pid, String arg) {
		String[] args = arg.split(":");
		if (args.length == 3 && args[0].equals("OK")) {
			String bukkit_version = args[1];
			String server_minecraft_version_s = args[2];
			MinecraftVersion server_minecraft_version = MinecraftVersion.getByVersion(server_minecraft_version_s);
			connection.getSession().getServerInfo().setServerMinecraftVersion(server_minecraft_version);

			connection.authorize();
			LogUtil.d("Auth success.");
			LogUtil.d("Server version:" + bukkit_version + ", Minecraft version:" + server_minecraft_version_s);

			AuthorizedData data = new AuthorizedData();
			data.setServerBukkitVersion(bukkit_version);
			data.setServerMinecraftVersion(server_minecraft_version_s);

			return data;
		} else {
			AuthorizedData data = new AuthorizedData();
			data.setSuccess(false);
			return data;
		}
	}
}
