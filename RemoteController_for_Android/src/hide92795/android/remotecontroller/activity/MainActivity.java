package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.ReceiveListener;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.receivedata.ServerData;
import hide92795.android.remotecontroller.ui.dialog.NotRecommendedVersionServerDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.NotRecommendedVersionServerDialogFragment.Callback;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener, ReceiveListener, Callback {
	private boolean first_show = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("MainActivity", "onCreate()");
		setContentView(R.layout.activity_main);
		setListener();

		// Check version
		boolean recommend_server = getIntent().getExtras().getBoolean("RECOMMENDED_SERVER_VERSION");
		if (!recommend_server) {
			NotRecommendedVersionServerDialogFragment dialog = new NotRecommendedVersionServerDialogFragment();
			dialog.show(getSupportFragmentManager(), "not_recommended_version_server_dialog");
		} else {
			initRequest();
		}
	}

	@Override
	public void onDialogClicked(boolean select_continue) {
		if (select_continue) {
			initRequest();
		} else {
			((Session) getApplication()).close(false, null);
			((Session) getApplication()).showProgressDialog(this, false, null);
			Intent intent = new Intent(this, LoginServerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("MODE", "DISCONNECT_BY_OWN");
			startActivity(intent);
		}
	}

	private void initRequest() {
		Connection connection = ((Session) getApplication()).getConnection();
		if (connection != null) {
			connection.requests.startReceiveConsoleLog();
			connection.requests.startReceiveChatLog();
			int pid = connection.requests.requestServerInfo();
			connection.addListener(pid, this);
		}
		((Session) getApplication()).showProgressDialog(this, false, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("MainActivity", "onDestroy()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("MainActivity", "onResume()");
		if (first_show) {
			first_show = false;
		} else {
			Connection connection = ((Session) getApplication()).getConnection();
			if (connection != null) {
				int pid = connection.requests.requestServerInfo();
				connection.addListener(pid, this);
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_config: {
			Intent i = new Intent(this, PreferenceActivity.class);
			startActivity(i);
			return true;
		}
		case R.id.menu_donate: {
			Intent i = new Intent(this, DonateActivity.class);
			startActivity(i);
			return true;
		}
		default:
		}
		return ret;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			((Session) getApplication()).close(false, null);
			((Session) getApplication()).showProgressDialog(this, false, null);
			Intent intent = new Intent(this, LoginServerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("MODE", "DISCONNECT_BY_OWN");
			startActivity(intent);
			return true;
		}
		return false;
	}

	private void setListener() {
		Button btn_players = (Button) findViewById(R.id.btn_main_players);
		btn_players.setOnClickListener(this);
		Button btn_console = (Button) findViewById(R.id.btn_main_console);
		btn_console.setOnClickListener(this);
		Button btn_chat = (Button) findViewById(R.id.btn_main_chat);
		btn_chat.setOnClickListener(this);
		Button btn_editfile = (Button) findViewById(R.id.btn_main_editfile);
		btn_editfile.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_main_players: {
			Intent intent = new Intent(this, OnlinePlayerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			startActivity(intent);
			break;
		}
		case R.id.btn_main_console: {
			Intent intent = new Intent(this, ConsoleActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			startActivity(intent);
			break;
		}
		case R.id.btn_main_chat: {
			Intent intent = new Intent(this, ChatActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			startActivity(intent);
			break;
		}
		case R.id.btn_main_editfile: {
			Intent intent = new Intent(this, EditFileActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onReceiveData(String sended_cmd, int pid, ReceiveData data) {
		if (sended_cmd.equals("SERVER_INFO")) {
			setServerInfo((ServerData) data);
			((Session) getApplication()).dismissProgressDialog();
		}
	}

	private void setServerInfo(ServerData data) {
		if (data.isSuccessed()) {
			TextView text_server_name = (TextView) findViewById(R.id.text_main_server_name);
			text_server_name.setText(data.getServername());
			TextView text_address = (TextView) findViewById(R.id.text_main_address);
			String address = ((Session) getApplication()).getConnection().getServerAddressWithPort();
			text_address.setText(address);
			TextView text_peoples = (TextView) findViewById(R.id.text_main_peoples);
			text_peoples.setText(data.getCurrent() + "/" + data.getMax());
			TextView text_add_info = (TextView) findViewById(R.id.text_main_add_infomation);
			text_add_info.setText(data.getAddInfo());
		} else {
			Toast.makeText(this, getString(R.string.str_error_on_server_data), Toast.LENGTH_SHORT).show();
		}
	}
}
