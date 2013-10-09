package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.PreferenceFragment;
import hide92795.android.remotecontroller.R;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;


public class PreferenceActivity extends android.preference.PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			onCreatePreferenceActivity();
		} else {
			onCreatePreferenceFragment();
		}
	}

	@SuppressWarnings("deprecation")
	private void onCreatePreferenceActivity() {
		addPreferencesFromResource(R.xml.pref);
	}

	@SuppressLint("NewApi")
	private void onCreatePreferenceFragment() {
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
	}

}
