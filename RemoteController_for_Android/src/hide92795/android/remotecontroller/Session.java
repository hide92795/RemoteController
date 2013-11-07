package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.activity.LoginServerActivity;
import hide92795.android.remotecontroller.ui.adapter.ChatListAdapter;
import hide92795.android.remotecontroller.ui.adapter.ConsoleListAdapter;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment.OnCancelListener;
import hide92795.android.remotecontroller.util.LogUtil;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class Session extends Application {
	private ArrayList<ConnectionData> saved_connection;
	private Connection connection;
	private CircleProgressDialogFragment dialog;
	private Handler handler;
	private ConsoleListAdapter console_adapter;
	private ChatListAdapter chat_adapter;
	private PlayerFaceManager face_manager;
	private ServerInfo server_info;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d("Session", "onCreate()");
		this.handler = new Handler();
		this.face_manager = new PlayerFaceManager(this);
		loadSavedConnection();
	}

	@SuppressWarnings("unchecked")
	private void loadSavedConnection() {
		try {
			FileInputStream fis = openFileInput("connection.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.saved_connection = (ArrayList<ConnectionData>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			this.saved_connection = new ArrayList<ConnectionData>();
			saveConnection();
		}
	}

	private void saveConnection() {
		try {
			FileOutputStream fos = openFileOutput("connection.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(saved_connection);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSavedConnection(ConnectionData connection_data) {
		saved_connection.add(connection_data);
		saveConnection();
	}

	public void removeSavedConnection(int position) {
		saved_connection.remove(position);
		saveConnection();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		LogUtil.d("Session", "onTerminate()");
		close(false, null);
	}

	public synchronized void close(boolean moveActivity, final String reason) {
		if (connection != null) {
			Connection conn = this.connection;
			connection = null;
			conn.close();
			if (moveActivity) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(Session.this, LoginServerActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("MODE", "DISCONNECT_BY_NETWORK_REASON");
						intent.putExtra("REASON", reason);
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

	public ArrayList<ConnectionData> getSavedConnection() {
		return saved_connection;
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
}
