package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.Session;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateServiceLaunchReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Session session = (Session) context.getApplicationContext();
		session.checkAutoUpdate();
	}

}
