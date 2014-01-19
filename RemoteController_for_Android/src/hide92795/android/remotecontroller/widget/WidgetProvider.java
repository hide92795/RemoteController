package hide92795.android.remotecontroller.widget;

import hide92795.android.remotecontroller.util.LogUtil;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

public class WidgetProvider extends AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		LogUtil.d("WidgetProvider#onEnabled()");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		LogUtil.d("WidgetProvider#onUpdate()");
		LogUtil.d("" + appWidgetIds.length);

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		LogUtil.d("WidgetProvider#onDeleted()");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		LogUtil.d("WidgetProvider#onDisabled()");
	}
}
