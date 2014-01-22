package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.autoupdate.AutoUpdateService;
import hide92795.android.remotecontroller.autoupdate.AutoUpdateServiceLaunchBroadcastReceiver;
import hide92795.android.remotecontroller.config.ConnectionConfig;
import hide92795.android.remotecontroller.ui.WidgetConfigFragment;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.Utils;
import hide92795.android.remotecontroller.widget.WidgetData;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;

public class WidgetConfigActivity extends PreferenceActivity implements OnClickListener, OnPreferenceChangeListener {
	private static final String PREFERENCE_NAME = "widget_temp";
	private int widget_id = AppWidgetManager.INVALID_APPWIDGET_ID;
	private PreferenceManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("WidgetConfigActivity#onCreate()");
		setContentView(R.layout.activity_widget_config);
		setListener();
		checkWidget();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			onCreateWidgetConfigActivity();
		} else {
			// This is deprecation method in API 11 and later, but I will not press the button.
			onCreateWidgetConfigActivity();
			// onCreateWidgetConfigFragment();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.d("WidgetConfigActivity#onStart()");
		GoogleAnalyticsUtil.startActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("WidgetConfigActivity#onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("WidgetConfigActivity#onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.d("WidgetConfigActivity#onStop()");
		GoogleAnalyticsUtil.stopActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("WidgetConfigActivity#onDestroy()");
		manager.getSharedPreferences().edit().clear().apply();
	}

	private void checkWidget() {
		setResult(RESULT_CANCELED);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			widget_id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if (widget_id == AppWidgetManager.INVALID_APPWIDGET_ID) {
			cancel();
		}
	}

	private void checkDefaultValue() {
		WidgetData data = ((Session) getApplication()).getWidgets().getWidgetDatas().get(widget_id);
		if (data != null) {
			LogUtil.d("Default value found.");
			SharedPreferences.Editor edit = manager.getSharedPreferences().edit();
			edit.putString(getString(R.string.conf_key_widget_account), data.getAccountUUID());
			edit.putInt(getString(R.string.conf_key_widget_background_color), data.getBackgroundColor());
			edit.putInt(getString(R.string.conf_key_widget_font_color), data.getFontColor());
			edit.commit();
		}
	}

	private void setListener() {
		Button btn_ok = (Button) findViewById(R.id.btn_widget_config_ok);
		Button btn_cancel = (Button) findViewById(R.id.btn_widget_config_cancel);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	private void onCreateWidgetConfigActivity() {
		changePreferenceLocation(getPreferenceManager());
		checkDefaultValue();
		addPreferencesFromResource(R.xml.widget_config);
		ListPreference list = (ListPreference) findPreference(getString(R.string.conf_key_widget_account));
		list.setOnPreferenceChangeListener(this);
		setDataToListPreference(list);
	}

	@SuppressLint("NewApi")
	private void onCreateWidgetConfigFragment() {
		getFragmentManager().beginTransaction().replace(android.R.id.content, new WidgetConfigFragment()).commit();
	}

	private void cancel() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private boolean checkAccountSelection(Preference preference, String uuid) {
		ConnectionConfig config = ((Session) getApplication()).getSavedConnection();
		if (uuid.length() == 0 || !config.getIds().contains(uuid)) {
			return false;
		}
		String name = config.getDatas().get(uuid).toString();
		preference.setSummary(name);
		return true;
	}

	public void changePreferenceLocation(PreferenceManager manager) {
		manager.setSharedPreferencesName(PREFERENCE_NAME);
		this.manager = manager;
	}

	public void setDataToListPreference(ListPreference pref) {
		String[] uuids = getSavedConnectionUUIDArray();
		String[] datas = getSavedConnectionDataArray(uuids);
		pref.setEntryValues(uuids);
		pref.setEntries(datas);

		String uuid = manager.getSharedPreferences().getString(getString(R.string.conf_key_widget_account), "");
		Button btn = (Button) findViewById(R.id.btn_widget_config_ok);
		if (uuid.length() != 0) {
			boolean valid = checkAccountSelection(pref, uuid);
			btn.setEnabled(valid);
		} else {
			btn.setEnabled(false);
		}
	}

	public String[] getSavedConnectionUUIDArray() {
		ArrayList<String> list = ((Session) getApplication()).getSavedConnection().getIds();
		String[] arr = list.toArray(new String[list.size()]);
		return arr;
	}

	public String[] getSavedConnectionDataArray(String[] uuids) {
		String[] datas = new String[uuids.length];
		ConnectionConfig conf = ((Session) getApplication()).getSavedConnection();
		for (int i = 0; i < uuids.length; i++) {
			String uuid = uuids[i];
			String data = conf.getDatas().get(uuid).toString();
			datas[i] = data;
		}
		return datas;
	}

	@Override
	public void onClick(View v) {
		LogUtil.d("WidgetConfigActivity#onClick()");
		switch (v.getId()) {
		case R.id.btn_widget_config_ok: {
			SharedPreferences pref = manager.getSharedPreferences();
			String account_uuid = pref.getString(getString(R.string.conf_key_widget_account), "");
			int background_color = pref.getInt(getString(R.string.conf_key_widget_background_color), getResources().getColor(R.color.translucent_black));
			int font_color = pref.getInt(getString(R.string.conf_key_widget_font_color), getResources().getColor(R.color.opaque_white));

			WidgetData data = new WidgetData();
			data.setAccountUUID(account_uuid);
			data.setBackgroundColor(background_color);
			data.setFontColor(font_color);

			((Session) getApplication()).addWidget(widget_id, data);

			LogUtil.d("Created widget.");
			LogUtil.d("Widget ID: " + widget_id);
			LogUtil.d("Account UUID :" + account_uuid);
			LogUtil.d("BackGround color: " + background_color);
			LogUtil.d("Font color: " + font_color);

			// Create first widget view
			RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_server_info_not_receive);
			view.setInt(R.id.image_widget_not_receive_background, "setAlpha", data.getBackGroungAlpha());
			view.setInt(R.id.image_widget_not_receive_background, "setColorFilter", data.getBackgroundColor());
			view.setTextColor(R.id.text_widget_not_receive_message, data.getFontColor());
			if (Utils.isServiceRunning(this, AutoUpdateService.class)) {
				view.setTextViewText(R.id.text_widget_not_receive_message, getString(R.string.str_waiting_for_first_reception));

				Intent i = new Intent(this, AutoUpdateServiceLaunchBroadcastReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				view.setOnClickPendingIntent(R.id.relative_widget_not_receive_entire, pi);
			} else {
				view.setTextViewText(R.id.text_widget_not_receive_message, getString(R.string.str_service_not_available));

				Intent i = new Intent(this, AutoUpdateManagerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				view.setOnClickPendingIntent(R.id.relative_widget_not_receive_entire, pi);
			}
			AppWidgetManager widget_manager = AppWidgetManager.getInstance(this);
			widget_manager.updateAppWidget(widget_id, view);

			Intent intent = new Intent();
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget_id);
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
		case R.id.btn_widget_config_cancel: {
			cancel();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(getString(R.string.conf_key_widget_account))) {
			Button btn = (Button) findViewById(R.id.btn_widget_config_ok);
			if (newValue == null) {
				btn.setEnabled(false);
				return false;
			}
			boolean valid = checkAccountSelection(preference, newValue.toString());
			btn.setEnabled(valid);
			return valid;
		}
		return false;
	}
}
