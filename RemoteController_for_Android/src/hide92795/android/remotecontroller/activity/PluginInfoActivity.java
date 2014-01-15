package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.ReceiveListener;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.ErrorData;
import hide92795.android.remotecontroller.receivedata.PluginInfoData;
import hide92795.android.remotecontroller.receivedata.PluginListData.PluginData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.ui.adapter.PluginInfoExpandableListAdapter;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class PluginInfoActivity extends ActionBarActivity implements ReceiveListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("PluginActivity#onCreate()");
		setContentView(R.layout.activity_plugin_info);
		requestPluginInfo();
	}

	private void requestPluginInfo() {
		Intent arg = getIntent();
		PluginData data = arg.getParcelableExtra("PLUGIN");
		((Session) getApplication()).showProgressDialog(this, false, null);
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestPluginInfo(data.name);
		connection.addListener(pid, this);
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
	public void onReceiveData(String sended_cmd, int pid, ReceiveData data) {
		if (sended_cmd.equals("PLUGIN_INFO")) {
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				setData((PluginInfoData) data);
			} else {
				ErrorData errordata = (ErrorData) data;
				Toast.makeText(this, getString(errordata.getMessageId(), errordata.getAddtionalInfo()), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void setData(PluginInfoData data) {
		PluginInfoExpandableListAdapter adapter = new PluginInfoExpandableListAdapter(this, data);
		ExpandableListView list = (ExpandableListView) findViewById(R.id.list_plugin_info_datas);
		list.setAdapter(adapter);
	}
}
