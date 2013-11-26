package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.receivedata.SuccessData;

public class CommandSuccess implements Command {
	private static final SuccessData data = new SuccessData();

	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		return data;
	}

}
