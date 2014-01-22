package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.ConnectionData;
import hide92795.android.remotecontroller.ConnectionDataPair;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.util.LogUtil;
import android.app.IntentService;
import android.content.Intent;

public class AutoUpdateDispatchService extends IntentService {

	public AutoUpdateDispatchService() {
		super("RemoteController AutoUpdate Dispatch Service");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d("AutoUpdateDispatchService#onCreate()");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LogUtil.d("AutoUpdateDispatchService#onHandleIntent()");
		String uuid = intent.getStringExtra("UUID");
		ConnectionData data = (ConnectionData) intent.getSerializableExtra("CONNECTION_DATA");
		if (data != null) {
			AutoUpdateConnection connection = new AutoUpdateConnection((Session) getApplication(), new ConnectionDataPair(uuid, data));
			connection.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d("AutoUpdateDispatchService#onDestroy()");
	}
}
