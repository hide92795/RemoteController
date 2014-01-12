package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.activity.LoginServerActivity;
import hide92795.android.remotecontroller.config.AutoUpdateConfig;
import hide92795.android.remotecontroller.config.ConnectionConfig;
import hide92795.android.remotecontroller.ui.adapter.ChatListAdapter;
import hide92795.android.remotecontroller.ui.adapter.ConsoleListAdapter;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment.OnCancelListener;
import hide92795.android.remotecontroller.ui.dialog.DisconnectDialogFragment;
import hide92795.android.remotecontroller.util.LogUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class Session extends Application {
	private ConnectionConfig saved_connection;
	private AutoUpdateConfig auto_update;
	private Connection connection;
	private CircleProgressDialogFragment dialog;
	private Handler handler;
	private ConsoleListAdapter console_adapter;
	private ChatListAdapter chat_adapter;
	private PlayerFaceManager face_manager;
	private ServerInfo server_info;
	public static AtomicBoolean debug;

	@Override
	public void onCreate() {
		super.onCreate();
		checkDebugPackageInstalled();
		LogUtil.d("Session#onCreate()");
		this.handler = new Handler();
		this.face_manager = new PlayerFaceManager(this);
		loadSavedConnection();
		loadAutoUpdate();
	}

	private void checkDebugPackageInstalled() {
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

	private void loadAutoUpdate() {
		try {
			FileInputStream fis = openFileInput("auto_update.v1.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.auto_update = (AutoUpdateConfig) ois.readObject();
		} catch (Exception e) {
			createNewAutoUpdate();
		}
	}

	private void createNewAutoUpdate() {
		this.auto_update = new AutoUpdateConfig();
		saveAutoUpdate();
	}

	public void addAutoUpdate(String uuid) {
		this.auto_update.getAutoUpdateList().add(uuid);
		saveAutoUpdate();
	}

	public void removeAutoUpdate(String uuid) {
		this.auto_update.getAutoUpdateList().remove(uuid);
		saveAutoUpdate();
	}

	private void saveAutoUpdate() {
		try {
			FileOutputStream fos = openFileOutput("auto_update.v1.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(auto_update);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadSavedConnection() {
		try {
			FileInputStream fis = openFileInput("connection.v2.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.saved_connection = (ConnectionConfig) ois.readObject();
		} catch (FileNotFoundException e) {
			try {
				FileInputStream fis = openFileInput("connection.dat");
				this.saved_connection = ConnectionDataMigration.v1Tov2(fis);
				saveConnection();
				fis.close();
			} catch (Exception e1) {
				createNewConnection();
			}
		} catch (OptionalDataException e) {
			createNewConnection();
		} catch (ClassNotFoundException e) {
			createNewConnection();
		} catch (IOException e) {
			createNewConnection();
		}
	}

	private void createNewConnection() {
		this.saved_connection = new ConnectionConfig();
		saveConnection();
	}

	private void saveConnection() {
		try {
			FileOutputStream fos = openFileOutput("connection.v2.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(saved_connection);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSavedConnection(ConnectionData connection_data) {
		String uuid = UUID.randomUUID().toString();
		saved_connection.getIds().add(uuid);
		saved_connection.getDatas().put(uuid, connection_data);
		saveConnection();
	}

	public void removeSavedConnection(int position) {
		String uuid = saved_connection.getIds().remove(position);
		saved_connection.getDatas().remove(uuid);
		saveConnection();
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


	public Handler getHandler() {
		return handler;
	}

	public ConnectionConfig getSavedConnection() {
		return saved_connection;
	}

	public AutoUpdateConfig getAutoUpdate() {
		return auto_update;
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
}
