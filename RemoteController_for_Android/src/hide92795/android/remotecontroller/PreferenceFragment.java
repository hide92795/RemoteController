package hide92795.android.remotecontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import hide92795.android.remotecontroller.R;


@SuppressLint("NewApi")
public class PreferenceFragment extends android.preference.PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
	}
}
