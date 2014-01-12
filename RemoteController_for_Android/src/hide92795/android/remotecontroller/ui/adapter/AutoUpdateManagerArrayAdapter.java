package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.ConnectionData;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.config.AutoUpdateConfig;
import hide92795.android.remotecontroller.config.ConnectionConfig;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AutoUpdateManagerArrayAdapter extends BaseAdapter {
	private Session session;
	private ConnectionConfig connections;
	private LayoutInflater inflater;

	static class ViewHolder {
		CheckBox name;
	}

	public AutoUpdateManagerArrayAdapter(Session session, ConnectionConfig connections) {
		this.session = session;
		this.connections = connections;
		this.inflater = (LayoutInflater) session.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return connections.getIds().size();
	}

	@Override
	public ConnectionData getItem(int position) {
		return connections.getDatas().get(connections.getIds().get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_auto_update_manager_object, null);
			CheckBox name = (CheckBox) view.findViewById(R.id.check_auto_update_check);

			holder = new ViewHolder();
			holder.name = name;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final String uuid = connections.getIds().get(position);
		ConnectionData item = getItem(position);

		if (item != null) {
			AutoUpdateConfig config = session.getAutoUpdate();

			holder.name.setText(item.toString());
			holder.name.setTag(uuid);

			if (config.getAutoUpdateList().contains(uuid)) {
				holder.name.setChecked(true);
			} else {
				holder.name.setChecked(false);
			}

			holder.name.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
					if (checked) {
						session.addAutoUpdate(uuid);
					} else {
						session.removeAutoUpdate(uuid);
					}
				}
			});
		}
		return view;
	}

}
