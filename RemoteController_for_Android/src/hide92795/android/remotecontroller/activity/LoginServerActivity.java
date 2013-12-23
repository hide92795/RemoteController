package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.ConnectionData;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.ReceiveListener;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.AuthorizedData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment.OnCancelListener;
import hide92795.android.remotecontroller.ui.dialog.DisconnectDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.NotRecommendedVersionServerDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.NotRecommendedVersionServerDialogFragment.Callback;
import hide92795.android.remotecontroller.util.LogUtil;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ViewSwitcher;
import com.google.analytics.tracking.android.EasyTracker;

public class LoginServerActivity extends FragmentActivity implements OnClickListener, TextWatcher, OnCancelListener, ReceiveListener, Callback {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("LoginServerActivity#onCreate()");
		setContentView(R.layout.activity_login_server);
		setListener();
		Bundle data = getIntent().getBundleExtra("DISCONNECT");
		if (data != null) {
			getIntent().removeExtra("DISCONNECT");
			showDisconnectDialog(data);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(getApplicationContext()).activityStart(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("LoginServerActivity#onResume()");
		checkSavedConnection();
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(getApplicationContext()).activityStop(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
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
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("LoginServerActivity#onDestroy()");
	}

	private void checkSavedConnection() {
		ArrayList<ConnectionData> saved_connection = ((Session) getApplication()).getSavedConnection();
		Spinner spinner = (Spinner) findViewById(R.id.spinner_login_exist_connection);
		ArrayAdapter<ConnectionData> adapter = new ArrayAdapter<ConnectionData>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (ConnectionData connection_data : saved_connection) {
			adapter.add(connection_data);
		}
		spinner.setAdapter(adapter);
		Button move_exist_connection = (Button) findViewById(R.id.btn_login_move_exist_connection);
		if (saved_connection.size() > 0) {
			changeDisplayToExistConnection();
			move_exist_connection.setEnabled(true);
		} else {
			changeDisplayToNewConnection();
			move_exist_connection.setEnabled(false);
		}
	}

	private void setListener() {
		findViewById(R.id.btn_login_move_new_connection).setOnClickListener(this);
		findViewById(R.id.btn_login_move_exist_connection).setOnClickListener(this);
		findViewById(R.id.btn_login_login_as_exist_connection).setOnClickListener(this);
		findViewById(R.id.btn_login_login_as_new_connection).setOnClickListener(this);
		((EditText) findViewById(R.id.edittext_login_address)).addTextChangedListener(this);
		((EditText) findViewById(R.id.edittext_login_port)).addTextChangedListener(this);
		((EditText) findViewById(R.id.edittext_login_username)).addTextChangedListener(this);
		((EditText) findViewById(R.id.edittext_login_password)).addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login_move_new_connection:
		case R.id.btn_login_move_exist_connection:
			ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher_login_panel);
			switcher.showNext();
			break;
		case R.id.btn_login_login_as_exist_connection: {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			Spinner spinner = (Spinner) findViewById(R.id.spinner_login_exist_connection);

			ConnectionData connection_data = (ConnectionData) spinner.getSelectedItem();

			login(connection_data);
			break;
		}
		case R.id.btn_login_login_as_new_connection: {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			CheckBox save = (CheckBox) findViewById(R.id.checkbox_login_save_connection);

			EditText v_address = (EditText) findViewById(R.id.edittext_login_address);
			EditText v_port = (EditText) findViewById(R.id.edittext_login_port);
			EditText v_username = (EditText) findViewById(R.id.edittext_login_username);
			EditText v_password = (EditText) findViewById(R.id.edittext_login_password);

			String address = v_address.getText().toString();
			int port = Integer.parseInt(v_port.getText().toString());
			String username = v_username.getText().toString();
			String password = v_password.getText().toString();

			ConnectionData connection_data = new ConnectionData();
			connection_data.setAddress(address);
			connection_data.setPort(port);
			connection_data.setUsername(username);
			connection_data.setPassword(password);

			if (save.isChecked()) {
				((Session) getApplication()).addSavedConnection(connection_data);
			}

			login(connection_data);
			break;
		}
		default:
			break;
		}
	}

	private void login(ConnectionData data) {
		LogUtil.startSaveLog((Session) getApplication());
		((Session) getApplication()).showProgressDialog(this, true, this);
		Connection connection = new Connection((Session) getApplication(), data);
		connection.addListener(-1, this);
		connection.start();
	}

	private void changeDisplayToExistConnection() {
		ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher_login_panel);
		if (switcher.getCurrentView().getId() != R.id.relative_login_exist_connection) {
			switcher.showNext();
		}
	}

	private void changeDisplayToNewConnection() {
		ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher_login_panel);
		if (switcher.getCurrentView().getId() != R.id.view_login_new_connection) {
			switcher.showNext();
		}
	}

	private void checkTextInput() {
		EditText v_address = (EditText) findViewById(R.id.edittext_login_address);
		EditText v_port = (EditText) findViewById(R.id.edittext_login_port);
		EditText v_username = (EditText) findViewById(R.id.edittext_login_username);
		EditText v_password = (EditText) findViewById(R.id.edittext_login_password);

		String address = v_address.getText().toString();
		String port = v_port.getText().toString();
		String username = v_username.getText().toString();
		String password = v_password.getText().toString();

		boolean b_address;
		if (address.length() == 0) {
			b_address = false;
		} else {
			b_address = true;
		}

		boolean b_port;
		try {
			int port_i = Integer.parseInt(port);
			if (port_i >= 0 && port_i <= 65535) {
				b_port = true;
			} else {
				b_port = false;
			}
		} catch (Exception e) {
			b_port = false;
		}

		boolean b_username;
		if (username.length() == 0) {
			b_username = false;
		} else {
			b_username = true;
		}

		boolean b_password;
		if (password.length() == 0) {
			b_password = false;
		} else {
			b_password = true;
		}

		Button btn = (Button) findViewById(R.id.btn_login_login_as_new_connection);
		if (b_address && b_port && b_username && b_password) {
			btn.setEnabled(true);
		} else {
			btn.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		checkTextInput();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onCancel() {
		((Session) getApplication()).close(false, null);
	}

	@Override
	public void onReceiveData(String sended_cmd, int pid, ReceiveData data) {
		if (pid == -1) {
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				// Success
				AuthorizedData auth_data = (AuthorizedData) data;
				if (((Session) getApplication()).getRecommendServerVersion().equals(auth_data.getServerBukkitVersion())) {
					startMainActivity();
				} else {
					NotRecommendedVersionServerDialogFragment dialog = new NotRecommendedVersionServerDialogFragment();
					dialog.show(getSupportFragmentManager(), "not_recommended_version_server_dialog");
				}
			} else {
				((Session) getApplication()).close(false, "");
				if (data instanceof AuthorizedData) {
					// Auth failed
					showDisconnectDialog(DisconnectDialogFragment.DISCONNECT_BY_SERVER, getString(R.string.str_auth_error));
				} else {
					// Some error
					showDisconnectDialog(DisconnectDialogFragment.DISCONNECT_BY_NETWORK_REASON, "");
				}
			}
		}
	}

	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		((Session) getApplication()).startActivity(intent);
	}

	@Override
	public void onDialogClicked(boolean select_continue) {
		if (select_continue) {
			startMainActivity();
		} else {
			((Session) getApplication()).close(false, "");
			showDisconnectDialog(DisconnectDialogFragment.DISCONNECT_BY_OWN, "");
		}
	}

	private void showDisconnectDialog(int mode, String reason) {
		LogUtil.stopSaveLog();
		Bundle b = new Bundle();
		b.putInt("MODE", mode);
		b.putString("REASON", reason);
		showDisconnectDialog(b);
	}

	private void showDisconnectDialog(Bundle data) {
		DisconnectDialogFragment dialog = new DisconnectDialogFragment();
		dialog.setArguments(data);
		dialog.show(getSupportFragmentManager(), "disconnect_dialog");
	}
}
