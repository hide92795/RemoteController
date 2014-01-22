package hide92795.android.remotecontroller.util;

import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null) {
			return cm.getActiveNetworkInfo().isConnected();
		}
		return false;
	}

	public static boolean isServiceRunning(Context context, Class<? extends Service> service_class) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningService = am.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo i : runningService) {
			if (i.service.getClassName().equals(service_class.getName())) {
				return true;
			}
		}
		return false;
	}
}
