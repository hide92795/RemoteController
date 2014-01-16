package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.ConnectionData;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.config.AutoUpdateConfig;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.Utils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoUpdateService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d("AutoUpdateService#onCreate()");
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
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
