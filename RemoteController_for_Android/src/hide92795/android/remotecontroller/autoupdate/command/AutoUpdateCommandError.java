package hide92795.android.remotecontroller.autoupdate.command;

import hide92795.android.remotecontroller.autoupdate.AutoUpdateConnection;
import hide92795.android.remotecontroller.receivedata.ErrorData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class AutoUpdateCommandError implements AutoUpdateCommand {
	@Override
	public ReceiveData doCommand(AutoUpdateConnection connection, int pid, String arg) {
		return new ErrorData("");
	}
}
