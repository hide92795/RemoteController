package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.NotificationData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.Base64Coder;
import java.nio.charset.Charset;

public class CommandNotification implements Command {
	@Override
	public ReceiveData doCommand(final Connection connection, int pid, final String arg) {
		connection.session.getHandler().post(new Runnable() {
			@Override
			public void run() {
				NotificationData data = new NotificationData();
				String[] datas = arg.split("-");
				String uuid = datas[0];
				String type = datas[1];
				String date = datas[2];
				String message = new String(Base64Coder.decode(datas[3]), Charset.forName("UTF-8"));

				data.setUUID(uuid);
				data.setType(type);
				data.setDate(date);
				data.setMessage(message);
				if (datas.length == 5) {
					data.setConsumed(false);
				} else {
					data.setConsumed(true);
				}
				connection.session.getNotificationAdapter().add(data);
			}
		});
		// pid is 0
		return null;
	}
}
