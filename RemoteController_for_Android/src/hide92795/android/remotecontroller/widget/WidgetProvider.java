package hide92795.android.remotecontroller.widget;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.activity.AutoUpdateManagerActivity;
import hide92795.android.remotecontroller.autoupdate.AutoUpdateService;
import hide92795.android.remotecontroller.autoupdate.AutoUpdateServiceLaunchBroadcastReceiver;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.Utils;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		LogUtil.d("WidgetProvider#onEnabled()");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
		super.onUpdate(context, manager, ids);
		LogUtil.d("WidgetProvider#onUpdate()");
		for (int widget_id : ids) {
			WidgetData data = ((Session) context.getApplicationContext()).getWidgets().getWidgetDatas().get(widget_id);
			RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_server_info_not_receive);
			if (data != null) {
				view.setInt(R.id.image_widget_not_receive_background, "setAlpha", data.getBackGroungAlpha());
				view.setInt(R.id.image_widget_not_receive_background, "setColorFilter", data.getBackgroundColor());
				view.setTextColor(R.id.text_widget_not_receive_message, data.getFontColor());
			}
			if (Utils.isServiceRunning(context, AutoUpdateService.class)) {
				view.setTextViewText(R.id.text_widget_not_receive_message, context.getString(R.string.str_waiting_for_first_reception));

				Intent i = new Intent(context, AutoUpdateServiceLaunchBroadcastReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				view.setOnClickPendingIntent(R.id.relative_widget_not_receive_entire, pi);
			} else {
				view.setTextViewText(R.id.text_widget_not_receive_message, context.getString(R.string.str_service_not_available));

				Intent i = new Intent(context, AutoUpdateManagerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				view.setOnClickPendingIntent(R.id.relative_widget_not_receive_entire, pi);
			}
			manager.updateAppWidget(widget_id, view);
		}
	}

	@Override
	public void onDeleted(Context context, int[] ids) {
		super.onDeleted(context, ids);
		LogUtil.d("WidgetProvider#onDeleted()");
		for (int id : ids) {
			((Session) context.getApplicationContext()).removeWigdet(id);
		}
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		LogUtil.d("WidgetProvider#onDisabled()");
	}
}
