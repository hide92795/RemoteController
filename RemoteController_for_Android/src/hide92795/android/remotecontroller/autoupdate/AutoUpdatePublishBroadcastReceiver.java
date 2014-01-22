package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.activity.LoginServerActivity;
import hide92795.android.remotecontroller.activity.WidgetConfigActivity;
import hide92795.android.remotecontroller.receivedata.NotificationUnreadCountData;
import hide92795.android.remotecontroller.receivedata.ServerData;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.widget.WidgetData;
import hide92795.android.remotecontroller.widget.WidgetProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class AutoUpdatePublishBroadcastReceiver extends BroadcastReceiver {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

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
		HashMap<Integer, WidgetData> widgets = ((Session) context.getApplicationContext()).getWidgets().getWidgetDatas();
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName provider = new ComponentName(context, WidgetProvider.class);
		int[] ids = manager.getAppWidgetIds(provider);
		for (int widget_id : ids) {
			WidgetData data = widgets.get(widget_id);
			if (data == null) {
				continue;
			}
			if (uuid.equals(data.getAccountUUID())) {
				updateWidget((Session) context.getApplicationContext(), manager, widget_id, data, uuid, address, server_info, notification_unread_count);
			}
		}
	}

	private void updateWidget(Session session, AppWidgetManager manager, int widget_id, WidgetData data, String uuid, String address, ServerData server_info,
			NotificationUnreadCountData notification_unread_count) {
		RemoteViews view = new RemoteViews(session.getPackageName(), R.layout.widget_server_info);
		view.setInt(R.id.image_widget_background, "setAlpha", data.getBackGroungAlpha());
		view.setInt(R.id.image_widget_background, "setColorFilter", data.getBackgroundColor());
		view.setTextColor(R.id.text_widget_server_name, data.getFontColor());
		view.setTextColor(R.id.text_widget_players, data.getFontColor());
		view.setTextColor(R.id.text_widget_notification, data.getFontColor());
		view.setTextColor(R.id.text_widget_last_update, data.getFontColor());
		view.setTextColor(R.id.const_text_widget_1, data.getFontColor());
		view.setTextColor(R.id.const_text_widget_2, data.getFontColor());
		view.setTextColor(R.id.const_text_widget_3, data.getFontColor());
		view.setTextColor(R.id.const_text_widget_4, data.getFontColor());
		view.setInt(R.id.btn_widget_config, "setColorFilter", data.getFontColor());
		view.setInt(R.id.btn_widget_refresh, "setColorFilter", data.getFontColor());

		view.setImageViewBitmap(R.id.image_widget_server_icon, session.getServerIconManager().getServerIcon(uuid));
		if (server_info != null) {
			view.setTextViewText(R.id.text_widget_status, session.getString(R.string.str_server_online));
			view.setTextColor(R.id.text_widget_status, session.getResources().getColor(R.color.color_enabled));
			view.setTextViewText(R.id.text_widget_server_name, server_info.getServername().split("\n")[0]);
			view.setTextViewText(R.id.text_widget_players, server_info.getCurrent() + "/" + server_info.getMax());
		} else {
			view.setTextViewText(R.id.text_widget_status, session.getString(R.string.str_server_offline));
			view.setTextColor(R.id.text_widget_status, session.getResources().getColor(R.color.red));
			view.setTextViewText(R.id.text_widget_server_name, address);
			view.setTextViewText(R.id.text_widget_players, session.getString(R.string.str_not_available));
		}
		if (notification_unread_count != null) {
			view.setTextViewText(R.id.text_widget_notification, Integer.toString(notification_unread_count.getCount()));
		} else {
			view.setTextViewText(R.id.text_widget_notification, session.getString(R.string.str_not_available));
		}

		// Create intent
		Intent i_launch = new Intent(session, LoginServerActivity.class);
		i_launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi_launch = PendingIntent.getActivity(session, 0, i_launch, PendingIntent.FLAG_CANCEL_CURRENT);

		Intent i_config = new Intent(session, WidgetConfigActivity.class);
		i_config.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i_config.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget_id);
		PendingIntent pi_config = PendingIntent.getActivity(session, 0, i_config, PendingIntent.FLAG_CANCEL_CURRENT);

		Intent i_refresh = new Intent(session, AutoUpdateDispatchService.class);
		i_refresh.putExtra("UUID", uuid);
		i_refresh.putExtra("CONNECTION_DATA", session.getSavedConnection().getDatas().get(uuid));
		PendingIntent pi_refresh = PendingIntent.getService(session, 0, i_refresh, PendingIntent.FLAG_UPDATE_CURRENT);

		view.setOnClickPendingIntent(R.id.relative_widget_entire, pi_launch);
		view.setOnClickPendingIntent(R.id.btn_widget_config, pi_config);
		view.setOnClickPendingIntent(R.id.btn_widget_refresh, pi_refresh);

		view.setTextViewText(R.id.text_widget_last_update, DATE_FORMAT.format(new Date()));
		manager.updateAppWidget(widget_id, view);
		LogUtil.d("[AutoUpdate] Widget updated");
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
		builder.setDefaults(getNotificationMethod(session));
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

		NotificationCompat.Builder builder = new NotificationCompat.Builder(session);
		builder.setContentIntent(pending_intent);
		builder.setTicker(message);
		builder.setSmallIcon(R.drawable.ic_stat_notify_notification);
		builder.setContentTitle(message);
		builder.setContentText(address);
		builder.setLargeIcon(session.getServerIconManager().getServerIcon(uuid));
		builder.setWhen(System.currentTimeMillis() + 10);
		builder.setDefaults(getNotificationMethod(session));
		NotificationManager manager = (NotificationManager) session.getSystemService(Service.NOTIFICATION_SERVICE);
		manager.notify(uuid.hashCode(), builder.build());
		LogUtil.d("[AutoUpdate] Notification showed");
	}

	private int getNotificationMethod(Session session) {
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
		return flag;
	}
}
