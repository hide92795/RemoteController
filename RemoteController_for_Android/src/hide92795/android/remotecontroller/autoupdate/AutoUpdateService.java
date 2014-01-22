package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.ConnectionData;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.activity.AutoUpdateManagerActivity;
import hide92795.android.remotecontroller.config.AutoUpdateConfig;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.Utils;
import hide92795.android.remotecontroller.widget.WidgetData;
import hide92795.android.remotecontroller.widget.WidgetProvider;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class AutoUpdateService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d("AutoUpdateService#onCreate()");
		updateWidgets(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d("AutoUpdateService#onStartCommand()");

		Session session = (Session) getApplication();
		AutoUpdateConfig config = session.getAutoUpdate();
		for (String uuid : config.getAutoUpdateList()) {
			ConnectionData data = session.getSavedConnection().getDatas().get(uuid);
			if (data != null) {
				if (Utils.isNetworkConnected(this)) {
					Intent i = new Intent(getApplicationContext(), AutoUpdateDispatchService.class);
					i.putExtra("UUID", uuid);
					i.putExtra("CONNECTION_DATA", data);
					startService(i);
					LogUtil.d("[AutoUpdate] AutoUpdate intent dispatched.");
				} else {
					// ネットワーク非接続
				}
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d("AutoUpdateService#onDestroy()");
		updateWidgets(false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void updateWidgets(boolean start) {
		ComponentName provider = new ComponentName(this, WidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		int[] ids = manager.getAppWidgetIds(provider);
		for (int widget_id : ids) {
			updateWidget(manager, widget_id, start);
		}
	}

	private void updateWidget(AppWidgetManager manager, int widget_id, boolean start) {
		WidgetData data = ((Session) getApplication()).getWidgets().getWidgetDatas().get(widget_id);
		RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_server_info_not_receive);
		if (data != null) {
			view.setInt(R.id.image_widget_not_receive_background, "setAlpha", data.getBackGroungAlpha());
			view.setInt(R.id.image_widget_not_receive_background, "setColorFilter", data.getBackgroundColor());
			view.setTextColor(R.id.text_widget_not_receive_message, data.getFontColor());
		}
		if (start) {
			view.setTextViewText(R.id.text_widget_not_receive_message, getString(R.string.str_waiting_for_first_reception));

			Intent i = new Intent(this, AutoUpdateServiceLaunchBroadcastReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
			view.setOnClickPendingIntent(R.id.relative_widget_not_receive_entire, pi);
		} else {
			view.setTextViewText(R.id.text_widget_not_receive_message, getString(R.string.str_service_not_available));

			Intent i = new Intent(this, AutoUpdateManagerActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
			view.setOnClickPendingIntent(R.id.relative_widget_not_receive_entire, pi);
		}
		AppWidgetManager widget_manager = AppWidgetManager.getInstance(this);
		widget_manager.updateAppWidget(widget_id, view);
	}
}
