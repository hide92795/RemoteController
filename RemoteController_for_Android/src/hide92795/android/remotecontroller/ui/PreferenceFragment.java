package hide92795.android.remotecontroller.ui;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.activity.PreferenceActivity;
import hide92795.android.remotecontroller.util.LogUtil;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;


@SuppressLint("NewApi")
public class PreferenceFragment extends android.preference.PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		PreferenceScreen open_log_dir_pref = (PreferenceScreen) findPreference("open_log_dir");
		try {
			open_log_dir_pref.setTitle(LogUtil.getLogDir((Session) getActivity().getApplication()).getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		open_log_dir_pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				((PreferenceActivity) getActivity()).copyLogDir();
				return true;
			}
		});
	}
}
