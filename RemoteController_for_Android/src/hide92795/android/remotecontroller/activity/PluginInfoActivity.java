package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.ReceiveListener;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.CommandData;
import hide92795.android.remotecontroller.receivedata.ErrorData;
import hide92795.android.remotecontroller.receivedata.PermissionData;
import hide92795.android.remotecontroller.receivedata.PluginInfoData;
import hide92795.android.remotecontroller.receivedata.PluginListData.PluginData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;

public class PluginInfoActivity extends FragmentActivity implements ReceiveListener {
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
		EasyTracker.getInstance(getApplicationContext()).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(getApplicationContext()).activityStop(this);
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
		TextView name = (TextView) findViewById(R.id.text_plugin_info_name);
		TextView status = (TextView) findViewById(R.id.text_plugin_info_status);
		TextView version = (TextView) findViewById(R.id.text_plugin_info_version);
		TextView author = (TextView) findViewById(R.id.text_plugin_info_author);
		TextView web = (TextView) findViewById(R.id.text_plugin_info_web);
		TextView description = (TextView) findViewById(R.id.text_plugin_info_description);

		setCommandsData(data.getCommands());
		setPermissionsData(data.getPermissions());

		name.setText(data.getName());
		if (data.isEnabled()) {
			status.setText(R.string.str_enabled);
			status.setTextColor(getResources().getColor(R.color.color_enabled));
		} else {
			status.setText(R.string.str_disabled);
			status.setTextColor(getResources().getColor(R.color.red));
		}
		version.setText(data.getVersion());
		author.setText(data.getAuthor());
		web.setText(data.getWeb());
		description.setText(data.getDescription());
	}

	private void setPermissionsData(PermissionData[] permissions) {
		TableLayout layout = (TableLayout) findViewById(R.id.table_plugin_info_permissions);
		for (PermissionData data : permissions) {
			TableRow row = new TableRow(this);
			View view = getLayoutInflater().inflate(R.layout.view_plugin_info_permission_object, null);

			TextView text_permission = (TextView) view.findViewById(R.id.text_plugin_info_permission_permission);
			TextView text_description = (TextView) view.findViewById(R.id.text_plugin_info_permission_description);

			text_permission.setText(data.getPermission());
			text_description.setText(data.getDescription());

			row.addView(view);
			layout.addView(row);
		}
	}

	private void setCommandsData(CommandData[] commands) {
		TableLayout layout = (TableLayout) findViewById(R.id.table_plugin_info_commands);
		for (CommandData data : commands) {
			TableRow row = new TableRow(this);
			View view = getLayoutInflater().inflate(R.layout.view_plugin_info_command_object, null);

			TextView text_command = (TextView) view.findViewById(R.id.text_plugin_info_command_command);
			TextView text_alias = (TextView) view.findViewById(R.id.text_plugin_info_command_alias);
			TextView text_description = (TextView) view.findViewById(R.id.text_plugin_info_command_desc);
			TextView text_permission = (TextView) view.findViewById(R.id.text_plugin_info_command_desc_permission);
			final TextView text_usage = (TextView) view.findViewById(R.id.text_plugin_info_command_desc_usage);

			text_command.setText("/" + data.getCommand());
			text_alias.setText(data.getAliases());
			text_description.setText(data.getDescription());
			text_permission.setText(data.getPermisson());
			text_usage.setText(data.getUsage());

			final RelativeLayout desc_layout = (RelativeLayout) view.findViewById(R.id.layout_plugin_info_command_desc);
			desc_layout.setVisibility(View.GONE);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ImageView indicator = (ImageView) v.findViewById(R.id.image_plugin_info_command_indicator);
					if (desc_layout.getVisibility() == View.VISIBLE) {
						desc_layout.setVisibility(View.GONE);
						indicator.setImageResource(R.drawable.collapsed);
					} else {
						desc_layout.setVisibility(View.VISIBLE);
						indicator.setImageResource(R.drawable.expanded);
					}
				}
			});
			row.addView(view);
			layout.addView(row);
		}

	}
}
