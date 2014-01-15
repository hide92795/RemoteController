package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.config.ConnectionConfig;
import hide92795.android.remotecontroller.ui.adapter.AutoUpdateManagerArrayAdapter;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class AutoUpdateManagerActivity extends FragmentActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("AutoUpdateManagerActivity#onCreate()");
		setContentView(R.layout.activity_auto_update_manager);

		setAdapeter();
		setListener();
		setData();
	}

	private void setAdapeter() {
		ListView list = (ListView) findViewById(R.id.list_auto_update_manager_list);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		Session session = ((Session) getApplication());
		ConnectionConfig connections = session.getSavedConnection();
		AutoUpdateManagerArrayAdapter adapter = new AutoUpdateManagerArrayAdapter(session, connections);
		list.setAdapter(adapter);
	}

	private void setListener() {
		Button edit = (Button) findViewById(R.id.btn_auto_update_edit);
		edit.setOnClickListener(this);
	}

	private void setData() {
		TextView text = (TextView) findViewById(R.id.text_auto_update_interval);
		EditText edit = (EditText) findViewById(R.id.edittext_auto_update_interval);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int interval_min = pref.getInt(ConfigKeys.AUTO_UPDATE_INTERVAL, ConfigDefaults.AUTO_UPDATE_INTERVAL);
		text.setText(String.valueOf(interval_min));
		edit.setText(String.valueOf(interval_min));
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalyticsUtil.startActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		((Session) getApplication()).checkAutoUpdate();
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalyticsUtil.stopActivity(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_auto_update_edit:
			Button btn = (Button) v;
			TextView text = (TextView) findViewById(R.id.text_auto_update_interval);
			EditText edit = (EditText) findViewById(R.id.edittext_auto_update_interval);
			ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.switcher_auto_update_interval);
			switcher.showNext();
			if (switcher.getCurrentView().getId() == R.id.text_auto_update_interval) {
				btn.setText(R.string.str_edit);
				// save
				int val = Integer.valueOf(edit.getText().toString());
				if (val > 0) {
					text.setText(edit.getText());
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
					pref.edit().putInt(ConfigKeys.AUTO_UPDATE_INTERVAL, val).commit();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					edit.setText(text.getText());
				}
			} else {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(edit, 0);
				edit.requestFocus();
				btn.setText(R.string.str_save);
			}
			break;
		default:
			break;
		}
	}
}
