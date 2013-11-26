package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.ErrorData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class CommandError implements Command {

	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		String[] messages = arg.split(":", 2);
		if (messages.length == 1) {
			return new ErrorData(arg);
		} else {
			return new ErrorData(messages[0], messages[1]);
		}
	}

}
