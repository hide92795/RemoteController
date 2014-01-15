package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.NotificationData;
import hide92795.android.remotecontroller.util.LogUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class NotificationActivity extends ActionBarActivity implements OnItemLongClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("NotificationActivity#onCreate()");
		setContentView(R.layout.activity_notification);
		setListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalyticsUtil.startActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalyticsUtil.stopActivity(this);
	}

	private void setListener() {
		ListView list = (ListView) findViewById(R.id.list_notification_notifications);
		list.setAdapter(((Session) getApplication()).getNotificationAdapter());
		list.setDivider(null);
		list.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_notification, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_notification_mark_as_read_all: {
			((Session) getApplication()).getConnection().requests.requestConsumeAllNotifications();
			((Session) getApplication()).getNotificationAdapter().consumeAll();
			return true;
		}
		default:
			break;
		}
		return ret;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		NotificationData data = ((Session) getApplication()).getNotificationAdapter().getItem(position);
		String uuid = data.getUUID();
		if (data.isConsumed()) {
			// Consumed -> Not consumed
			data.setConsumed(false);
			((Session) getApplication()).getConnection().requests.requestChangeNotificationConsumeState(uuid, false);
		} else {
			// Not consumed -> Consumed
			data.setConsumed(true);
			((Session) getApplication()).getConnection().requests.requestChangeNotificationConsumeState(uuid, true);
		}
		((Session) getApplication()).getNotificationAdapter().notifyDataSetChanged();
		return true;
	}

}
