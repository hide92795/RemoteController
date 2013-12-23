package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.PlayerFaceUpdator;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.ReceiveListener;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.ErrorData;
import hide92795.android.remotecontroller.receivedata.PlayersData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.ui.adapter.PlayersExpandableListAdapter;
import hide92795.android.remotecontroller.ui.adapter.PlayersExpandableListAdapter.OnPlayerHandleClickListener;
import hide92795.android.remotecontroller.ui.dialog.PlayerDialogFragment;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;

public class OnlinePlayerActivity extends FragmentActivity implements ReceiveListener, PlayerFaceUpdator.Callback, OnPlayerHandleClickListener, PlayerDialogFragment.Callback {
	private PlayersExpandableListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_players);
		LogUtil.d("OnlinePlayerActivity#onCreate()");
		this.adapter = new PlayersExpandableListAdapter(getApplication());
		initPlayersList();
		updateOnlinePlayers();
	}

	private void initPlayersList() {
		ExpandableListView list = (ExpandableListView) findViewById(R.id.list_players_players);
		list.setAdapter(adapter);
		adapter.setOnPlayerHandleClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("OnlinePlayerActivity#onDestroy()");
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(getApplicationContext()).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(getApplicationContext()).activityStop(this);
	}

	private void updateOnlinePlayers() {
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestOnlinePlayers();
		connection.addListener(pid, this);
		((Session) getApplication()).showProgressDialog(this, false, null);
	}

	@Override
	public void onReceiveData(String sended_cmd, int pid, ReceiveData data) {
		if (sended_cmd.equals("PLAYERS")) {
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				adapter.clear();
				String[] users = ((PlayersData) data).getPlayers();
				TextView text = (TextView) findViewById(R.id.text_players_infomation);
				if (users.length == 0) {
					text.setVisibility(View.VISIBLE);
					text.setText(R.string.str_no_user_logged_in);
					ListView list = (ListView) findViewById(R.id.list_players_players);
					list.setVisibility(View.GONE);
					return;
				}
				text.setVisibility(View.GONE);

				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				String url = pref.getString(ConfigKeys.PLAYER_FACE_IMAGE_URL, ConfigDefaults.PLAYER_FACE_IMAGE_URL);
				PlayerFaceUpdator updator = new PlayerFaceUpdator(this, ((Session) getApplication()).getFaceManager(), url);
				updator.execute(users);
				adapter.addAll(users);
				adapter.notifyDataSetChanged();
			}
		} else if (sended_cmd.equals("KICK") || sended_cmd.equals("BAN") || sended_cmd.equals("GIVE") || sended_cmd.equals("GAMEMODE")) {
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				Toast.makeText(this, R.string.str_success, Toast.LENGTH_SHORT).show();
			} else {
				ErrorData errordata = (ErrorData) data;
				Toast.makeText(this, getString(errordata.getMessageId(), errordata.getAddtionalInfo()), Toast.LENGTH_SHORT).show();
			}
			updateOnlinePlayers();
		}
	}

	@Override
	public void onProgress() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onFinish() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onPlayerHandleClick(String username, int handle_id) {
		switch (handle_id) {
		case R.id.btn_player_handle_kick:
		case R.id.btn_player_handle_ban:
		case R.id.btn_player_handle_give:
		case R.id.btn_player_handle_gamemode:
			FragmentManager manager = getSupportFragmentManager();
			PlayerDialogFragment fragment = new PlayerDialogFragment();
			Bundle b = new Bundle();
			b.putString("USERNAME", username);
			b.putInt("HANDLE_ID", handle_id);
			fragment.setArguments(b);
			fragment.show(manager, "handle_dialog");
			break;
		default:
			break;
		}
	}

	@Override
	public void onDialogClicked(String username, int handle_id, Bundle data) {
		switch (handle_id) {
		case R.id.btn_player_handle_kick: {
			Connection connection = ((Session) getApplication()).getConnection();
			int pid = connection.requests.requestKick(username, data.getString("REASON"));
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
			break;
		}
		case R.id.btn_player_handle_ban: {
			Connection connection = ((Session) getApplication()).getConnection();
			int pid = connection.requests.requestBan(username, data.getString("REASON"));
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
			break;
		}
		case R.id.btn_player_handle_give: {
			Connection connection = ((Session) getApplication()).getConnection();
			String item = data.getString("ITEM");
			String num_s = data.getString("NUM");
			int num;
			if (num_s == null || num_s.length() == 0) {
				num = 1;
			} else {
				num = Integer.valueOf(num_s);
			}
			int pid = connection.requests.requestGive(username, item, num);
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
			break;
		}
		case R.id.btn_player_handle_gamemode: {
			Connection connection = ((Session) getApplication()).getConnection();
			int pid = connection.requests.requestGamemode(username, data.getInt("GAMEMODE"));
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
			break;
		}
		default:
			break;
		}

	}
}
