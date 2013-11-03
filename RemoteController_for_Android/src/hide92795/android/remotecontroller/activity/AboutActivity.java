package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.util.LogUtil;
import java.io.InputStream;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("AboutActivity", "onCreate");
		setContentView(R.layout.activity_about);

		TextView version = (TextView) findViewById(R.id.text_about_version);
		try {
			version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e1) {
		}

		TextView the_android_open_source_project_license = (TextView) findViewById(R.id.text_about_the_android_open_source_project_license);
		try {
			Resources res = getResources();
			InputStream in_s = res.openRawResource(R.raw.the_android_open_source_project);
			byte[] b = new byte[in_s.available()];
			in_s.read(b);
			the_android_open_source_project_license.setText(new String(b));
		} catch (Exception e) {
			the_android_open_source_project_license.setText("Error: can't show license.");
		}

		TextView android_color_picker_preference = (TextView) findViewById(R.id.text_about_android_color_picker_preference_license);
		try {
			Resources res = getResources();
			InputStream in_s = res.openRawResource(R.raw.android_color_picker_preference);
			byte[] b = new byte[in_s.available()];
			in_s.read(b);
			android_color_picker_preference.setText(new String(b));
		} catch (Exception e) {
			android_color_picker_preference.setText("Error: can't show license.");
		}

		TextView java_websocket = (TextView) findViewById(R.id.text_about_java_websocket);
		try {
			Resources res = getResources();
			InputStream in_s = res.openRawResource(R.raw.java_websocket);
			byte[] b = new byte[in_s.available()];
			in_s.read(b);
			java_websocket.setText(new String(b));
		} catch (Exception e) {
			java_websocket.setText("Error: can't show license.");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("AboutActivity", "onDestroy");
	}
}
