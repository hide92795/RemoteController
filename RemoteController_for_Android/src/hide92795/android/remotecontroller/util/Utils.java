package hide92795.android.remotecontroller.util;

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
}
