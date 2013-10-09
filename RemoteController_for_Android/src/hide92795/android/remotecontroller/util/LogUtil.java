package hide92795.android.remotecontroller.util;

import android.util.Log;

public class LogUtil {
	private static final boolean debug = false;

	public static final void d(String tag, String msg) {
		if (debug) {
			Log.d(tag, msg);
		}
	}
}
