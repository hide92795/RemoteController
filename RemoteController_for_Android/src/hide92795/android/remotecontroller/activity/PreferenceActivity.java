package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.ui.PreferenceFragment;
import hide92795.android.remotecontroller.util.LogUtil;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;


public class PreferenceActivity extends android.preference.PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			onCreatePreferenceActivity();
		} else {
			onCreatePreferenceFragment();
		}
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

	@SuppressWarnings("deprecation")
	private void onCreatePreferenceActivity() {
		addPreferencesFromResource(R.xml.pref);
		PreferenceScreen open_log_dir_pref = (PreferenceScreen) findPreference("open_log_dir");
		try {
			open_log_dir_pref.setSummary(LogUtil.getLogDir((Session) getApplication()).getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		open_log_dir_pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				copyLogDir();
				return true;
			}
		});
	}

	@SuppressLint("NewApi")
	private void onCreatePreferenceFragment() {
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();

	}

	public void copyLogDir() {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				copyToClip();
			} else {
				copyToClipUnderHONEYCOMB();
			}
			Toast.makeText(this, R.string.str_copied, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, R.string.str_copied, Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("deprecation")
	protected void copyToClipUnderHONEYCOMB() throws IOException {
		android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clipboardManager.setText(LogUtil.getLogDir((Session) getApplication()).getCanonicalPath());
	}

	@SuppressLint("NewApi")
	protected void copyToClip() throws IOException {
		android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData.Item item = new ClipData.Item(LogUtil.getLogDir((Session) getApplication()).getCanonicalPath());
		String[] mimeTypes = new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN };
		ClipData clip = new ClipData("data", mimeTypes, item);
		clipboardManager.setPrimaryClip(clip);
	}
}
