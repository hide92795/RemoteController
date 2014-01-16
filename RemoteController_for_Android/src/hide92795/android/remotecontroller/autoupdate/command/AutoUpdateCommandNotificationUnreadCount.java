package hide92795.android.remotecontroller.autoupdate.command;

import hide92795.android.remotecontroller.autoupdate.AutoUpdateConnection;
import hide92795.android.remotecontroller.receivedata.NotificationUnreadCountData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class AutoUpdateCommandNotificationUnreadCount implements AutoUpdateCommand {
	@Override
	public ReceiveData doCommand(AutoUpdateConnection connection, int pid, String arg) {
		int count = Integer.valueOf(arg);
		NotificationUnreadCountData data = new NotificationUnreadCountData();
		data.setCount(count);
		return data;
	}
}
