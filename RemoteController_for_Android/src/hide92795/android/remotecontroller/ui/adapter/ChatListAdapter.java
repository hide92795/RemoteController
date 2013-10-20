package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.ChatData;
import hide92795.android.remotecontroller.util.ConfigDefaults;
import hide92795.android.remotecontroller.util.ConfigKeys;
import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {
	private final Session session;
	private final ArrayList<ChatData> chat;
	private LayoutInflater inflater;
	private OnAddChatListener listener;

	static class ViewHolder {
		TextView message;
	}

	public ChatListAdapter(Session session) {
		this.session = session;
		this.chat = new ArrayList<ChatData>();
		this.inflater = (LayoutInflater) session.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_chat_object, null);
			TextView message = (TextView) view.findViewById(R.id.text_chat_message);

			holder = new ViewHolder();
			holder.message = message;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ChatData item = getItem(position);

		if (item != null) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(session);
			int fontsize = Integer.parseInt(pref.getString(ConfigKeys.CHAT_FONT_SIZE, ConfigDefaults.CHAT_FONT_SIZE));
			int backgroundcolor = pref.getInt(ConfigKeys.CHAT_BACKGOUND_COLOR, ConfigDefaults.CHAT_BACKGOUND_COLOR);
			int fontcolor = pref.getInt(ConfigKeys.CHAT_FONT_COLOR, ConfigDefaults.CHAT_FONT_COLOR);

			holder.message.setText(item.getMessage());
			holder.message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);

			view.setBackgroundColor(backgroundcolor);
			holder.message.setTextColor(fontcolor);

			if (pref.getBoolean(ConfigKeys.CHAT_ELLIPSIZE, ConfigDefaults.CHAT_ELLIPSIZE)) {
				// 折り返す
				holder.message.setHorizontallyScrolling(false);
				holder.message.setEllipsize(TruncateAt.MARQUEE);
			} else {
				// 折り返さない(省略)
				holder.message.setHorizontallyScrolling(true);
				holder.message.setEllipsize(TruncateAt.END);
			}
		}
		return view;
	}

	public void add(ChatData object) {
		chat.add(object);
		notifyDataSetChanged();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(session);
		if (pref.getBoolean(ConfigKeys.CHAT_MOVE_BOTTOM, ConfigDefaults.CHAT_MOVE_BOTTOM)) {
			if (listener != null) {
				listener.onAddChat();
			}
		}
	}


	public void setOnAddChatListener(OnAddChatListener listener) {
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return chat.size();
	}

	@Override
	public ChatData getItem(int position) {
		return chat.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public interface OnAddChatListener {
		void onAddChat();
	}
}
