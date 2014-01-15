package hide92795.android.remotecontroller.autoupdate.command;

import hide92795.android.remotecontroller.autoupdate.AutoUpdateConnection;
import hide92795.android.remotecontroller.receivedata.AuthorizedData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.LogUtil;

public class AutoUpdateCommandAuthentication implements AutoUpdateCommand {
	@Override
	public ReceiveData doCommand(AutoUpdateConnection connection, int pid, String arg) {
		String[] args = arg.split(":");
		if (args.length == 3 && args[0].equals("OK")) {
			String bukkit_version = args[1];
			String server_minecraft_version_s = args[2];

			connection.authorize();
			LogUtil.d("[AutoUpdate] Auth success.");
			LogUtil.d("[AutoUpdate] Server version:" + bukkit_version + ", Minecraft version:" + server_minecraft_version_s);

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
