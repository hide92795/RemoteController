package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateServiceLaunchBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d("AutoUpdateServiceLaunchBroadcastReceiver#onReceive()");
		Session session = (Session) context.getApplicationContext();
		session.checkAutoUpdate();
	}
}
