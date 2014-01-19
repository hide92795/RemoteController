package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.activity.LoginServerActivity;
import hide92795.android.remotecontroller.receivedata.NotificationUnreadCountData;
import hide92795.android.remotecontroller.receivedata.ServerData;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class AutoUpdatePublishBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d("AutoUpdatePublishBroadcastReceiver#onReceive()");
		String uuid = intent.getStringExtra("UUID");
		String address = intent.getStringExtra("ADDRESS");
		ServerData server_info = (ServerData) intent.getSerializableExtra("SERVER_INFO");
		NotificationUnreadCountData notification_unread_count = (NotificationUnreadCountData) intent.getSerializableExtra("NOTIFICATION_UNREAD_COUNT");

		// Android notification
		// No error & count is 0 -> not notify
		if (server_info == null) {
			// Error
			createNotificationError((Session) context.getApplicationContext(), uuid, address);
		} else if (notification_unread_count == null) {
			// Error
			createNotificationError((Session) context.getApplicationContext(), uuid, address);
		} else if (notification_unread_count.getCount() != 0) {
			// has notification
			createNotification((Session) context.getApplicationContext(), uuid, address, server_info, notification_unread_count);
		} else {
			// no notification
			removeNotification((Session) context.getApplicationContext(), uuid);
		}

		// Widget
	}

	private void removeNotification(Session session, String uuid) {
		NotificationManager manager = (NotificationManager) session.getSystemService(Service.NOTIFICATION_SERVICE);
		manager.cancel(uuid.hashCode());
	}

	private void createNotificationError(Session session, String uuid, String address) {
		Intent launch_app = new Intent(session, LoginServerActivity.class);
		launch_app.putExtra("NOTIFICATION", uuid.hashCode());
		launch_app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pending_intent = PendingIntent.getActivity(session, 0, launch_app, PendingIntent.FLAG_UPDATE_CURRENT);

		String message = session.getString(R.string.str_error_on_server_data);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(session);
		builder.setContentIntent(pending_intent);
		builder.setTicker(message);
		builder.setSmallIcon(R.drawable.ic_stat_notify_notification);
		builder.setContentTitle(message);
		builder.setContentText(address);
		builder.setLargeIcon(session.getServerIconManager().getServerIcon(uuid));
		builder.setWhen(System.currentTimeMillis() + 10);
		builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
		NotificationManager manager = (NotificationManager) session.getSystemService(Service.NOTIFICATION_SERVICE);
		manager.notify(uuid.hashCode(), builder.build());
		LogUtil.d("[AutoUpdate] Notification showed");
	}

	private void createNotification(Session session, String uuid, String address, ServerData server_info, NotificationUnreadCountData notification_unread_count) {
		Intent launch_app = new Intent(session, LoginServerActivity.class);
		launch_app.putExtra("NOTIFICATION", uuid.hashCode());
		launch_app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pending_intent = PendingIntent.getActivity(session, 0, launch_app, PendingIntent.FLAG_UPDATE_CURRENT);

		String message;
		if (notification_unread_count == null) {
			message = session.getString(R.string.str_no_notification_unread);
		} else {
			int count = notification_unread_count.getCount();
			message = String.format(session.getResources().getQuantityString(R.plurals.str_plurals_notification_unread, count), count);
		}

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(session);
		int flag = 0;
		if (pref.getBoolean(ConfigKeys.AUTO_UPDATE_NOTIFICATION_SOUND, ConfigDefaults.AUTO_UPDATE_NOTIFICATION_SOUND)) {
			flag = flag | Notification.DEFAULT_SOUND;
		}
		if (pref.getBoolean(ConfigKeys.AUTO_UPDATE_NOTIFICATION_VIBRATE, ConfigDefaults.AUTO_UPDATE_NOTIFICATION_VIBRATE)) {
			flag = flag | Notification.DEFAULT_VIBRATE;
		}
		if (pref.getBoolean(ConfigKeys.AUTO_UPDATE_NOTIFICATION_LIGHT, ConfigDefaults.AUTO_UPDATE_NOTIFICATION_LIGHT)) {
			flag = flag | Notification.DEFAULT_LIGHTS;
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(session);
		builder.setContentIntent(pending_intent);
		builder.setTicker(message);
		builder.setSmallIcon(R.drawable.ic_stat_notify_notification);
		builder.setContentTitle(message);
		builder.setContentText(address);
		builder.setLargeIcon(session.getServerIconManager().getServerIcon(uuid));
		builder.setWhen(System.currentTimeMillis() + 10);
		builder.setDefaults(flag);
		NotificationManager manager = (NotificationManager) session.getSystemService(Service.NOTIFICATION_SERVICE);
		manager.notify(uuid.hashCode(), builder.build());
		LogUtil.d("[AutoUpdate] Notification showed");
	}
}
