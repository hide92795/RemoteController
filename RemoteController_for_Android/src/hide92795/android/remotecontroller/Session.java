package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.activity.LoginServerActivity;
import hide92795.android.remotecontroller.autoupdate.AutoUpdateService;
import hide92795.android.remotecontroller.config.AutoUpdateConfig;
import hide92795.android.remotecontroller.config.ConnectionConfig;
import hide92795.android.remotecontroller.config.WidgetConfig;
import hide92795.android.remotecontroller.ui.adapter.ChatListAdapter;
import hide92795.android.remotecontroller.ui.adapter.ConsoleListAdapter;
import hide92795.android.remotecontroller.ui.adapter.NotificationListAdapter;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment.OnCancelListener;
import hide92795.android.remotecontroller.ui.dialog.DisconnectDialogFragment;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.Utils;
import hide92795.android.remotecontroller.widget.WidgetData;
import hide92795.android.remotecontroller.widget.WidgetProvider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class Session extends Application {
	public static AtomicBoolean debug;
	// Config
	private ConnectionConfig config_saved_connection;
	private AutoUpdateConfig config_auto_update;
	private WidgetConfig config_widgets;
	// Manager
	private PlayerFaceManager face_manager;
	private ServerIconManager server_icon_manager;
	// Adapter
	private ConsoleListAdapter console_adapter;
	private ChatListAdapter chat_adapter;
	private NotificationListAdapter notification_adapter;
	// Others
	private Connection connection;
	private ServerInfo server_info;
	private CircleProgressDialogFragment dialog;
	private Handler handler;

	@Override
	public void onCreate() {
		super.onCreate();
		checkDebugPackageInstalled();
		LogUtil.d("Session#onCreate()");
		this.handler = new Handler();
		this.face_manager = new PlayerFaceManager(this);
		this.server_icon_manager = new ServerIconManager(this);
		loadSavedConnectionConfig();
		loadAutoUpdateConfig();
		loadWidgetsConfig();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		LogUtil.d("Session#onTerminate()");
		this.face_manager.dispose();
		this.server_icon_manager.dispose();
	}

	private void checkDebugPackageInstalled() {
		Log.d("RemoteController", "----------------------------------------------------");
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo("hide92795.android.debug", PackageManager.GET_ACTIVITIES);
			debug = new AtomicBoolean(true);
			Log.d("RemoteController", "Debug mode enabled!");
		} catch (NameNotFoundException e) {
			debug = new AtomicBoolean(false);
			Log.d("RemoteController", "Debug mode disabled!");
		}
	}

	private void loadAutoUpdateConfig() {
		try {
			FileInputStream fis = openFileInput("config_auto_update.v1.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.config_auto_update = (AutoUpdateConfig) ois.readObject();
		} catch (Exception e) {
			createNewAutoUpdateConfig();
		}
	}

	private void createNewAutoUpdateConfig() {
		this.config_auto_update = new AutoUpdateConfig();
		saveAutoUpdateConfig();
	}

	private void saveAutoUpdateConfig() {
		try {
			FileOutputStream fos = openFileOutput("config_auto_update.v1.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(config_auto_update);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadSavedConnectionConfig() {
		try {
			FileInputStream fis = openFileInput("connection.v2.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.config_saved_connection = (ConnectionConfig) ois.readObject();
		} catch (FileNotFoundException e) {
			try {
				FileInputStream fis = openFileInput("connection.dat");
				this.config_saved_connection = ConnectionDataMigration.v1Tov2(fis);
				saveConnectionConfig();
				fis.close();
			} catch (Exception e1) {
				createNewConnectionConfig();
			}
		} catch (OptionalDataException e) {
			createNewConnectionConfig();
		} catch (ClassNotFoundException e) {
			createNewConnectionConfig();
		} catch (IOException e) {
			createNewConnectionConfig();
		}
	}

	private void createNewConnectionConfig() {
		this.config_saved_connection = new ConnectionConfig();
		saveConnectionConfig();
	}

	private void saveConnectionConfig() {
		try {
			FileOutputStream fos = openFileOutput("connection.v2.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(config_saved_connection);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadWidgetsConfig() {
		try {
			FileInputStream fis = openFileInput("widgets.v1.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.config_widgets = (WidgetConfig) ois.readObject();
			checkValidWidgets();
		} catch (Exception e) {
			createNewWidgetsConfig();
		}
	}

	private void createNewWidgetsConfig() {
		this.config_widgets = new WidgetConfig();
		saveWidgetsConfig();
	}

	private void saveWidgetsConfig() {
		checkValidWidgets();
		try {
			FileOutputStream fos = openFileOutput("widgets.v1.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(config_widgets);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkValidWidgets() {
		ComponentName provider = new ComponentName(this, WidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		int[] ids = manager.getAppWidgetIds(provider);
		Arrays.sort(ids);
		HashSet<Integer> saved_ids = new HashSet<Integer>(config_widgets.getWidgetDatas().keySet());
		for (int saved_id : saved_ids) {
			if (Arrays.binarySearch(ids, saved_id) < 0) {
				removeWigdet(saved_id);
			}
		}
	}

	public void addSavedConnection(ConnectionData connection_data) {
		String uuid = UUID.randomUUID().toString();
		config_saved_connection.getIds().add(uuid);
		config_saved_connection.getDatas().put(uuid, connection_data);
		saveConnectionConfig();
	}

	public void removeSavedConnection(int position) {
		String uuid = config_saved_connection.getIds().remove(position);
		config_saved_connection.getDatas().remove(uuid);
		saveConnectionConfig();
	}

	public void addAutoUpdate(String uuid) {
		this.config_auto_update.getAutoUpdateList().add(uuid);
		saveAutoUpdateConfig();
	}

	public void removeAutoUpdate(String uuid) {
		this.config_auto_update.getAutoUpdateList().remove(uuid);
		saveAutoUpdateConfig();
	}

	public void addWidget(int widget_id, WidgetData data) {
		this.config_widgets.getWidgetDatas().put(widget_id, data);
		saveWidgetsConfig();
	}

	public void removeWigdet(int widget_id) {
		this.config_widgets.getWidgetDatas().remove(widget_id);
		saveWidgetsConfig();
	}

	public synchronized void close(boolean moveActivity, final String reason) {
		if (connection != null) {
			Connection conn = this.connection;
			connection = null;
			conn.close();
			LogUtil.stopSaveLog();
			if (moveActivity) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Session.this, LoginServerActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle data = new Bundle();
						data.putInt("MODE", DisconnectDialogFragment.DISCONNECT_BY_SERVER);
						data.putString("REASON", reason);
						intent.putExtra("DISCONNECT", data);
						startActivity(intent);
					}
				});
			}
		}
	}

	public void setConnection(Connection connection) {
		close(false, null);
		this.console_adapter = new ConsoleListAdapter(this);
		this.chat_adapter = new ChatListAdapter(this);
		this.notification_adapter = new NotificationListAdapter(this);
		this.server_info = new ServerInfo();
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public ConsoleListAdapter getConsoleAdapter() {
		return console_adapter;
	}

	public ChatListAdapter getChatAdapter() {
		return chat_adapter;
	}

	public NotificationListAdapter getNotificationAdapter() {
		return notification_adapter;
	}

	public Handler getHandler() {
		return handler;
	}

	public ConnectionConfig getSavedConnection() {
		return config_saved_connection;
	}

	public AutoUpdateConfig getAutoUpdate() {
		return config_auto_update;
	}

	public WidgetConfig getWidgets() {
		return config_widgets;
	}

	public void showProgressDialog(FragmentActivity activity, boolean cancelable, OnCancelListener listener) {
		dismissProgressDialog();
		dialog = new CircleProgressDialogFragment();
		dialog.setOnCancelListener(listener);
		dialog.setCancelable(cancelable);
		FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
		ft.add(dialog, "progress_dialog");
		ft.commitAllowingStateLoss();
	}

	public void dismissProgressDialog() {
		if (dialog != null) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
			}
			dialog = null;
		}
	}

	public PlayerFaceManager getFaceManager() {
		return face_manager;
	}

	public ServerIconManager getServerIconManager() {
		return server_icon_manager;
	}

	public String getRecommendServerVersion() {
		return getString(R.string.info_recommend_server_version);
	}

	public ServerInfo getServerInfo() {
		return server_info;
	}

	public static boolean isDebug() {
		if (debug == null) {
			return false;
		} else {
			return debug.get();
		}
	}

	public void checkAutoUpdate() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		long interval_min = pref.getInt(ConfigKeys.AUTO_UPDATE_INTERVAL, ConfigDefaults.AUTO_UPDATE_INTERVAL);
		long interval = interval_min * 60L * 1000L;

		Intent intent = new Intent(this, AutoUpdateService.class);
		intent.setAction("RemoteController AutoUpdate");
		PendingIntent pendingIntent = PendingIntent.getService(this, 92795, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);

		if (config_auto_update.getAutoUpdateList().size() == 0) {
			stopService(intent);
			am.cancel(pendingIntent);
		} else {
			am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10, interval, pendingIntent);
		}
	}

	public void startAutoUpdateAlarmManagerOnlyNotStarted() {
		if (!Utils.isServiceRunning(this, AutoUpdateService.class)) {
			checkAutoUpdate();
		}
	}
}
