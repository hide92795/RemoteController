package hide92795.android.remotecontroller.autoupdate.command;

import hide92795.android.remotecontroller.autoupdate.AutoUpdateConnection;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public interface AutoUpdateCommand {
	ReceiveData doCommand(AutoUpdateConnection connection, int pid, String arg);
}
