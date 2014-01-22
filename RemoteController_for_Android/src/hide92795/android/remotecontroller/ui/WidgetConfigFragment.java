package hide92795.android.remotecontroller.ui;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.activity.WidgetConfigActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.ListPreference;

@SuppressLint("NewApi")
public class WidgetConfigFragment extends android.preference.PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((WidgetConfigActivity) getActivity()).changePreferenceLocation(getPreferenceManager());
		addPreferencesFromResource(R.xml.widget_config);
		ListPreference list = (ListPreference) findPreference(getString(R.string.conf_key_widget_account));
		list.setOnPreferenceChangeListener((WidgetConfigActivity) getActivity());
		((WidgetConfigActivity) getActivity()).setDataToListPreference(list);
	}
}