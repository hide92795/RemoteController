package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.util.LogUtil;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoUpdateService extends Service {

	@Override
	public void onCreate() {
		LogUtil.d("AutoUpdateService#onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d("AutoUpdateService#onStartCommand()");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		LogUtil.d("AutoUpdateService#onDestroy()");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
