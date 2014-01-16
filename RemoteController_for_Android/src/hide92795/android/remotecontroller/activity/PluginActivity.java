package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.ReceiveListener;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.ErrorData;
import hide92795.android.remotecontroller.receivedata.PluginListData;
import hide92795.android.remotecontroller.receivedata.PluginListData.PluginData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.ui.adapter.PluginExpandableListAdapter;
import hide92795.android.remotecontroller.ui.adapter.PluginExpandableListAdapter.OnPluginHandleClickListener;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class PluginActivity extends ActionBarActivity implements ReceiveListener, OnPluginHandleClickListener {
	private PluginExpandableListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("PluginActivity#onCreate()");
		setContentView(R.layout.activity_plugin_list);
		setListener();
		((Session) getApplication()).showProgressDialog(this, false, null);
		requestPluginList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("PluginActivity#onDestroy()");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_plugin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_plugin_refresh: {
			((Session) getApplication()).showProgressDialog(this, false, null);
			requestPluginList();
			return true;
		}
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	private void setListener() {
		ExpandableListView list = (ExpandableListView) findViewById(R.id.list_plugin_list);
		adapter = new PluginExpandableListAdapter(this);
		adapter.setOnPluginHandleClickListener(this);
		list.setAdapter(adapter);
	}

	private void requestPluginList() {
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestPluginList();
		connection.addListener(pid, this);
	}

	@Override
	public void onReceiveData(String sended_cmd, int pid, ReceiveData data) {
		if (sended_cmd.equals("PLUGIN_LIST")) {
			adapter.clear();
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				PluginListData plugins = (PluginListData) data;
				adapter.addAll(plugins.getPlugins());
				adapter.notifyDataSetChanged();
			} else {
				ErrorData errordata = (ErrorData) data;
				Toast.makeText(this, getString(errordata.getMessageId(), errordata.getAddtionalInfo()), Toast.LENGTH_SHORT).show();
			}
		} else if (sended_cmd.equals("PLUGIN_STATE")) {
			requestPluginList();
			if (data.isSuccessed()) {
				Toast.makeText(this, R.string.str_success, Toast.LENGTH_SHORT).show();
			} else {
				ErrorData errordata = (ErrorData) data;
				Toast.makeText(this, getString(errordata.getMessageId(), errordata.getAddtionalInfo()), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onPluginHandleClick(PluginData pluginData, int handle_id) {
		switch (handle_id) {
		case R.id.btn_plugin_handle_view: {
			Intent i = new Intent(this, PluginInfoActivity.class);
			i.putExtra("PLUGIN", pluginData);
			startActivity(i);
			break;
		}
		case R.id.btn_plugin_handle_enable: {
			Connection connection = ((Session) getApplication()).getConnection();
			int pid = connection.requests.requestChangePluginState(pluginData.getName(), true);
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
			break;
		}
		case R.id.btn_plugin_handle_disable: {
			Connection connection = ((Session) getApplication()).getConnection();
			int pid = connection.requests.requestChangePluginState(pluginData.getName(), false);
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
			break;
		}
		default:
			break;
		}
	}
}
