package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.ui.adapter.ConsoleListAdapter;
import hide92795.android.remotecontroller.ui.adapter.ConsoleListAdapter.OnAddConsoleListener;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;

public class ConsoleActivity extends FragmentActivity implements OnClickListener, OnAddConsoleListener, OnKeyListener {
	private ScaleGestureDetector gesture_detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("ConsoleActivity#onCreate()");
		setContentView(R.layout.activity_console);
		gesture_detector = new ScaleGestureDetector(this, onScaleGestureListener);
		setListener();
		setColor();
	}

	private void setListener() {
		ListView list = (ListView) findViewById(R.id.list_console_console);
		list.setAdapter(((Session) getApplication()).getConsoleAdapter());
		list.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesture_detector.onTouchEvent(event);
				return false;
			}
		});
		// list.setOnItemLongClickListener(this);
		list.setSelection(list.getCount() - 1);
		Button btn_send = (Button) findViewById(R.id.btn_console_send);
		btn_send.setOnClickListener(this);
		EditText editText = (EditText) findViewById(R.id.edittext_console_send);
		editText.setOnKeyListener(this);
	}

	private void setColor() {
		ListView list = (ListView) findViewById(R.id.list_console_console);
		list.setDivider(null);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		list.setBackgroundColor(pref.getInt(ConfigKeys.CONSOLE_BACKGOUND_COLOR, ConfigDefaults.CONSOLE_BACKGOUND_COLOR));
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("ConsoleActivity#onResume()");
		((Session) getApplication()).getConsoleAdapter().setOnAddConsoleListener(this);
		((Session) getApplication()).getConsoleAdapter().notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("ConsoleActivity#onPause()");
		((Session) getApplication()).getConsoleAdapter().setOnAddConsoleListener(null);
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean b_show_date = pref.getBoolean(ConfigKeys.CONSOLE_DATE, ConfigDefaults.CONSOLE_DATE);
		boolean b_show_log_level = pref.getBoolean(ConfigKeys.CONSOLE_LOG_LEVEL, ConfigDefaults.CONSOLE_LOG_LEVEL);
		boolean b_ellipsize = pref.getBoolean(ConfigKeys.CONSOLE_ELLIPSIZE, ConfigDefaults.CONSOLE_ELLIPSIZE);
		boolean b_move_bottom = pref.getBoolean(ConfigKeys.CONSOLE_MOVE_BOTTOM, ConfigDefaults.CONSOLE_MOVE_BOTTOM);
		MenuItem date = menu.findItem(R.id.menu_console_date);
		MenuItem level = menu.findItem(R.id.menu_console_level);
		MenuItem ellipsize = menu.findItem(R.id.menu_console_ellipsize);
		MenuItem move_bottom = menu.findItem(R.id.menu_console_move_bottom);
		date.setChecked(b_show_date);
		level.setChecked(b_show_log_level);
		ellipsize.setChecked(b_ellipsize);
		move_bottom.setChecked(b_move_bottom);

		// HONEYCOMB以下だとチェックボックスがつかないので自前で追加
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (b_show_date) {
				date.setIcon(R.drawable.ic_checked);
			} else {
				date.setIcon(R.drawable.ic_not_checked);
			}
			if (b_show_log_level) {
				level.setIcon(R.drawable.ic_checked);
			} else {
				level.setIcon(R.drawable.ic_not_checked);
			}
			if (b_ellipsize) {
				ellipsize.setIcon(R.drawable.ic_checked);
			} else {
				ellipsize.setIcon(R.drawable.ic_not_checked);
			}
			if (b_move_bottom) {
				move_bottom.setIcon(R.drawable.ic_checked);
			} else {
				move_bottom.setIcon(R.drawable.ic_not_checked);
			}
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_console, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_console_date: {
			boolean date = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CONSOLE_DATE, date).commit();
			updateConsoleAdapter();
			return true;
		}
		case R.id.menu_console_level: {
			boolean level = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CONSOLE_LOG_LEVEL, level).commit();
			updateConsoleAdapter();
			return true;
		}
		case R.id.menu_console_ellipsize: {
			boolean ellipsize = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CONSOLE_ELLIPSIZE, ellipsize).commit();
			updateConsoleAdapter();
			return true;
		}
		case R.id.menu_console_move_bottom: {
			boolean move_bottom = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CONSOLE_MOVE_BOTTOM, move_bottom).commit();
			updateConsoleAdapter();
			return true;
		}
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConsoleAdapter() {
		ListView list = (ListView) findViewById(R.id.list_console_console);
		ConsoleListAdapter adapter = (ConsoleListAdapter) list.getAdapter();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_console_send: {
			EditText edittext = (EditText) findViewById(R.id.edittext_console_send);
			String cmd = edittext.getText().toString();
			((Session) getApplication()).getConnection().requests.sendConsoleCommand(cmd);
			edittext.setText("");
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onAddConsole() {
		ListView list = (ListView) findViewById(R.id.list_console_console);
		list.setSelection(list.getCount() - 1);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (v.getId() == R.id.edittext_console_send) {
			if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
				String cmd = ((TextView) v).getText().toString();
				((Session) getApplication()).getConnection().requests.sendConsoleCommand(cmd);
				((TextView) v).setText("");
				return true;
			}
		}
		return false;
	}

	private final SimpleOnScaleGestureListener onScaleGestureListener = new SimpleOnScaleGestureListener() {
		private int fontsize;

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ConsoleActivity.this);
			fontsize = Integer.parseInt(pref.getString(ConfigKeys.CONSOLE_FONT_SIZE, ConfigDefaults.CONSOLE_FONT_SIZE));
			return super.onScaleBegin(gesture_detector);
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			int calced = Math.round(fontsize * detector.getScaleFactor());
			if (calced == 0) {
				calced = 1;
			}
			if (fontsize != calced) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ConsoleActivity.this);
				pref.edit().putString(ConfigKeys.CONSOLE_FONT_SIZE, Integer.toString(calced)).commit();
				((Session) getApplication()).getConsoleAdapter().notifyDataSetChanged();
			}
			return super.onScale(gesture_detector);
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			int calced = Math.round(fontsize * detector.getScaleFactor());
			if (calced == 0) {
				calced = 1;
			}
			if (fontsize != calced) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ConsoleActivity.this);
				pref.edit().putString(ConfigKeys.CONSOLE_FONT_SIZE, Integer.toString(calced)).commit();
				((Session) getApplication()).getConsoleAdapter().notifyDataSetChanged();
			}
		}
	};
}
