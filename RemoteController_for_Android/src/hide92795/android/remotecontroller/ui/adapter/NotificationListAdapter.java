package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.NotificationData;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotificationListAdapter extends BaseAdapter {
	private final ArrayList<NotificationData> notifications;
	private LayoutInflater inflater;
	private OnAddNotificationListener listener;

	static class ViewHolder {
		TextView date;
		TextView type;
		TextView message;
	}

	public NotificationListAdapter(Session session) {
		this.notifications = new ArrayList<NotificationData>();
		this.inflater = (LayoutInflater) session.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return notifications.size();
	}

	@Override
	public NotificationData getItem(int position) {
		return notifications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_notification_object, null);
			TextView date = (TextView) view.findViewById(R.id.text_notification_date);
			TextView type = (TextView) view.findViewById(R.id.text_notification_type);
			TextView message = (TextView) view.findViewById(R.id.text_notification_message);

			holder = new ViewHolder();
			holder.date = date;
			holder.type = type;
			holder.message = message;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		NotificationData item = getItem(position);

		if (item != null) {
			holder.date.setText(item.getDate());
			holder.type.setText(item.getTypeStringID());
			holder.message.setText(item.getMessage());
			if (item.isConsumed()) {
				holder.date.setTypeface(Typeface.DEFAULT);
				holder.type.setTypeface(Typeface.DEFAULT);
				holder.message.setTypeface(Typeface.DEFAULT);
			} else {
				holder.date.setTypeface(Typeface.DEFAULT_BOLD);
				holder.type.setTypeface(Typeface.DEFAULT_BOLD);
				holder.message.setTypeface(Typeface.DEFAULT_BOLD);
			}
		}
		return view;
	}

	public void add(NotificationData object) {
		notifications.add(0, object);
		notifyDataSetChanged();
		if (listener != null) {
			listener.onAddNotification();
		}
	}

	public void setOnAddNotificationListener(OnAddNotificationListener listener) {
		this.listener = listener;
	}

	public boolean hasNotConsumedNotification() {
		for (NotificationData notification : notifications) {
			if (!notification.isConsumed()) {
				return true;
			}
		}
		return false;
	}

	public void consumeAll() {
		for (NotificationData notification : notifications) {
			notification.setConsumed(true);
		}
		notifyDataSetChanged();
	}

	public interface OnAddNotificationListener {
		void onAddNotification();
	}
}
