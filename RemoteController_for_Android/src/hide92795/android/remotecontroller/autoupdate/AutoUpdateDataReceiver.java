package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommand;
import hide92795.android.remotecontroller.receivedata.AuthorizedData;
import hide92795.android.remotecontroller.receivedata.NotificationUnreadCountData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.receivedata.ServerData;
import hide92795.android.remotecontroller.receivedata.ServerIconData;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;

public class AutoUpdateDataReceiver {
	private final Session session;
	private final AutoUpdateConnection connection;
	private final String uuid;

	// pids
	private int request_server_info;
	private int request_notification_unread_count;
	private int request_server_icon;

	// received flags
	private boolean received_server_info;
	private boolean received_notification_unread_count;
	private boolean received_server_icon;

	// received datas
	private ServerData server_info_data;
	private NotificationUnreadCountData notification_unread_count_data;


	public AutoUpdateDataReceiver(Session session, AutoUpdateConnection connection, String uuid) {
		this.session = session;
		this.connection = connection;
		this.uuid = uuid;
	}

	public void onReceive(AutoUpdateCommand command, int pid, String raw_data) {
		ReceiveData data = command.doCommand(connection, pid, raw_data);
		switch (pid) {
		case 0:
			// data is null
			break;
		case -1:
			// Auth finish
			// Start to receive data
			AuthorizedData auth_data = (AuthorizedData) data;
			if (auth_data.isSuccessed()) {
				requestData();
			} else {
				connection.close();
			}
			break;
		default:
			if (pid == request_server_info) {
				received_server_info = true;
				if (data.isSuccessed()) {
					server_info_data = (ServerData) data;
				}
				checkAllReceived();
			} else if (pid == request_notification_unread_count) {
				received_notification_unread_count = true;
				if (data.isSuccessed()) {
					notification_unread_count_data = (NotificationUnreadCountData) data;
				}
				checkAllReceived();
			} else if (pid == request_server_icon) {
				received_server_icon = true;
				if (data.isSuccessed()) {
					ServerIconData server_icon_data = (ServerIconData) data;
					if (server_icon_data.getIcon() != null) {
						session.getServerIconManager().addServerIcon(uuid, server_icon_data.getIcon());
					} else {
						session.getServerIconManager().addServerIconDefault(uuid);
					}
				}
				checkAllReceived();
			}
			break;
		}
	}

	private void requestData() {
		request_server_info = connection.requests.requestServerInfo();
		request_notification_unread_count = connection.requests.requestNotificationUnreadCount();
		request_server_icon = connection.requests.requestServerIcon();
	}

	private void checkAllReceived() {
		if (received_server_info && received_notification_unread_count && received_server_icon) {
			connection.close();
		}
	}

	public void publish() {
		Intent i = new Intent("hide92795.android.remotecontroller.NOTIFICATION_PUBLISH");
		i.putExtra("UUID", uuid);
		i.putExtra("ADDRESS", connection.toString());
		i.putExtra("SERVER_INFO", server_info_data);
		i.putExtra("NOTIFICATION_UNREAD_COUNT", notification_unread_count_data);
		session.sendBroadcast(i);
		LogUtil.d("[AutoUpdate] Sended broadcast");
	}
}
