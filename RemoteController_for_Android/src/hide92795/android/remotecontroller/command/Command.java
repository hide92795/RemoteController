package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public interface Command {
	ReceiveData doCommand(Connection connection, int pid, String arg);
}
