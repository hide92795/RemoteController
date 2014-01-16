package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.ui.adapter.ChatListAdapter;
import hide92795.android.remotecontroller.ui.adapter.ChatListAdapter.OnAddChatListener;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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

public class ChatActivity extends ActionBarActivity implements OnClickListener, OnAddChatListener, OnKeyListener {
	private ScaleGestureDetector gesture_detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("ChatActivity#onCreate()");
		setContentView(R.layout.activity_chat);
		gesture_detector = new ScaleGestureDetector(this, onScaleGestureListener);
		setListener();
		setColor();
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.d("ChatActivity#onStart()");
		GoogleAnalyticsUtil.startActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("ChatActivity#onResume()");
		((Session) getApplication()).getChatAdapter().setOnAddChatListener(this);
		((Session) getApplication()).getChatAdapter().notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("ChatActivity#onPause()");
		((Session) getApplication()).getChatAdapter().setOnAddChatListener(null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.d("ChatActivity#onStop()");
		GoogleAnalyticsUtil.stopActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("ChatActivity#onDestroy()");
	}

	private void setListener() {
		ListView list = (ListView) findViewById(R.id.list_chat_chat);
		list.setAdapter(((Session) getApplication()).getChatAdapter());
		list.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesture_detector.onTouchEvent(event);
				return false;
			}
		});
		// list.setOnItemLongClickListener(this);
		list.setSelection(list.getCount() - 1);
		Button btn_send = (Button) findViewById(R.id.btn_chat_send);
		btn_send.setOnClickListener(this);
		EditText editText = (EditText) findViewById(R.id.edittext_chat_send);
		editText.setOnKeyListener(this);
	}

	private void setColor() {
		ListView list = (ListView) findViewById(R.id.list_chat_chat);
		list.setDivider(null);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		list.setBackgroundColor(pref.getInt(ConfigKeys.CHAT_BACKGOUND_COLOR, ConfigDefaults.CHAT_BACKGOUND_COLOR));
	}

	private void updateConsoleAdapter() {
		ListView list = (ListView) findViewById(R.id.list_chat_chat);
		ChatListAdapter adapter = (ChatListAdapter) list.getAdapter();
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean b_ellipsize = pref.getBoolean(ConfigKeys.CHAT_ELLIPSIZE, ConfigDefaults.CHAT_ELLIPSIZE);
		boolean b_move_bottom = pref.getBoolean(ConfigKeys.CHAT_MOVE_BOTTOM, ConfigDefaults.CHAT_MOVE_BOTTOM);
		MenuItem ellipsize = menu.findItem(R.id.menu_chat_ellipsize);
		MenuItem move_bottom = menu.findItem(R.id.menu_chat_move_bottom);
		ellipsize.setChecked(b_ellipsize);
		move_bottom.setChecked(b_move_bottom);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_chat_ellipsize: {
			boolean ellipsize = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CHAT_ELLIPSIZE, ellipsize).commit();
			updateConsoleAdapter();
			return true;
		}
		case R.id.menu_chat_move_bottom: {
			boolean move_bottom = !item.isChecked();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigKeys.CHAT_MOVE_BOTTOM, move_bottom).commit();
			updateConsoleAdapter();
			return true;
		}
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_chat_send: {
			EditText edittext = (EditText) findViewById(R.id.edittext_chat_send);
			String message = edittext.getText().toString();
			if (message.length() != 0) {
				((Session) getApplication()).getConnection().requests.requestChat(message);
				edittext.setText("");
				GoogleAnalyticsUtil.dispatchChat(this);
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onAddChat() {
		ListView list = (ListView) findViewById(R.id.list_chat_chat);
		list.setSelection(list.getCount() - 1);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (v.getId() == R.id.edittext_chat_send) {
			if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
				String message = ((TextView) v).getText().toString();
				if (message.length() != 0) {
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					((Session) getApplication()).getConnection().requests.requestChat(message);
					((TextView) v).setText("");
					return true;
				}
			}
		}
		return false;
	}

	private final SimpleOnScaleGestureListener onScaleGestureListener = new SimpleOnScaleGestureListener() {
		private int fontsize;

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
			fontsize = Integer.parseInt(pref.getString(ConfigKeys.CHAT_FONT_SIZE, ConfigDefaults.CHAT_FONT_SIZE));
			return super.onScaleBegin(gesture_detector);
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			int calced = Math.round(fontsize * detector.getScaleFactor());
			if (calced == 0) {
				calced = 1;
			}
			if (fontsize != calced) {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
				pref.edit().putString(ConfigKeys.CHAT_FONT_SIZE, Integer.toString(calced)).commit();
				((Session) getApplication()).getChatAdapter().notifyDataSetChanged();
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
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
				pref.edit().putString(ConfigKeys.CHAT_FONT_SIZE, Integer.toString(calced)).commit();
				((Session) getApplication()).getChatAdapter().notifyDataSetChanged();
			}
		}
	};
}
