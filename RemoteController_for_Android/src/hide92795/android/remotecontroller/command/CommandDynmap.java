package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.DynmapData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class CommandDynmap implements Command {
	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		String address;
		boolean enabled;
		try {
			int port = Integer.parseInt(arg);
			if (port == 0) {
				enabled = false;
			} else {
				enabled = true;
			}
			address = "http://" + connection.getServerAddress() + ":" + port;
		} catch (Exception e) {
			enabled = true;
			address = arg;
		}
		DynmapData data = new DynmapData();
		data.setEnable(enabled);
		data.setAddress(address);
		return data;
	}
}
