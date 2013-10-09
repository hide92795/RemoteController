package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.data.ConnectionData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AccountManagerListAdapter extends BaseAdapter {
	private final Session session;
	private LayoutInflater inflater;

	static class ViewHolder {
		TextView num;
		TextView address;
		TextView username;
	}

	public AccountManagerListAdapter(Session session) {
		this.session = session;
		this.inflater = (LayoutInflater) session.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return session.getSavedConnection().size();
	}

	@Override
	public ConnectionData getItem(int position) {
		return session.getSavedConnection().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_account_manager_object, null);
			TextView num = (TextView) view.findViewById(R.id.text_account_manager_num);
			TextView address = (TextView) view.findViewById(R.id.text_account_manager_address);
			TextView username = (TextView) view.findViewById(R.id.text_account_manager_username);

			holder = new ViewHolder();
			holder.num = num;
			holder.address = address;
			holder.username = username;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ConnectionData item = getItem(position);

		if (item != null) {
			holder.num.setText((position + 1) + ":");
			holder.address.setText(item.getAddress() + ":" + item.getPort());
			holder.username.setText(item.getUsername());
		}
		return view;
	}

	public void moveUp(int position) {
		if (position == 0) {
			return;
		}
		ConnectionData data = getItem(position);
		session.getSavedConnection().remove(position);
		session.getSavedConnection().add(position - 1, data);
		notifyDataSetChanged();
	}

	public void moveDown(int position) {
		if (position == session.getSavedConnection().size() - 1) {
			return;
		}
		ConnectionData data = getItem(position);
		session.getSavedConnection().remove(position);
		session.getSavedConnection().add(position + 1, data);
		notifyDataSetChanged();
	}
}
