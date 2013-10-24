package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.DynmapData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.LogUtil;

public class CommandDynmap implements Command {
	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		LogUtil.d("CommandDynmap", arg);
		int port = Integer.parseInt(arg);
		boolean enabled;
		if (port == 0) {
			enabled = false;
		} else {
			enabled = true;
		}
		DynmapData data = new DynmapData();
		data.setEnable(enabled);
		data.setPort(port);
		return data;
	}
}
