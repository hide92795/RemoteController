package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.ConsoleData;
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

public class ConsoleListAdapter extends BaseAdapter {
	private final Session session;
	private final ArrayList<ConsoleData> console;
	private LayoutInflater inflater;
	private OnAddConsoleListener listener;

	static class ViewHolder {
		TextView date;
		TextView log_level;
		TextView text;
	}

	public ConsoleListAdapter(Session session) {
		this.session = session;
		this.console = new ArrayList<ConsoleData>();
		this.inflater = (LayoutInflater) session.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_console_object, null);
			TextView date = (TextView) view.findViewById(R.id.text_console_date);
			TextView log_level = (TextView) view.findViewById(R.id.text_console_log_level);
			TextView text = (TextView) view.findViewById(R.id.text_console_text);

			holder = new ViewHolder();
			holder.date = date;
			holder.log_level = log_level;
			holder.text = text;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ConsoleData item = getItem(position);

		if (item != null) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(session);
			int fontsize = Integer.parseInt(pref.getString(ConfigKeys.CONSOLE_FONT_SIZE, ConfigDefaults.CONSOLE_FONT_SIZE));

			if (pref.getBoolean(ConfigKeys.CONSOLE_DATE, ConfigDefaults.CONSOLE_DATE)) {
				// 日時表示
				holder.date.setVisibility(View.VISIBLE);
				holder.date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
				holder.date.setText(item.getDate());
			} else {
				// 日時非表示
				holder.date.setVisibility(View.GONE);
			}
			if (pref.getBoolean(ConfigKeys.CONSOLE_LOG_LEVEL, ConfigDefaults.CONSOLE_LOG_LEVEL)) {
				// ログレベル表示
				holder.log_level.setVisibility(View.VISIBLE);
				holder.log_level.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
				holder.log_level.setText(item.getLogLevel());
			} else {
				// ログレベル非表示
				holder.log_level.setVisibility(View.GONE);
			}

			holder.text.setText(item.getText());
			holder.text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);

			if (pref.getBoolean(ConfigKeys.CONSOLE_ELLIPSIZE, ConfigDefaults.CONSOLE_ELLIPSIZE)) {
				// 折り返す
				holder.text.setHorizontallyScrolling(false);
				holder.text.setEllipsize(TruncateAt.MARQUEE);
			} else {
				// 折り返さない(省略)
				holder.text.setHorizontallyScrolling(true);
				holder.text.setEllipsize(TruncateAt.END);
			}
		}
		return view;
	}

	public void add(ConsoleData object) {
		console.add(object);
		notifyDataSetChanged();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(session);
		if (pref.getBoolean(ConfigKeys.CONSOLE_MOVE_BOTTOM, ConfigDefaults.CONSOLE_MOVE_BOTTOM)) {
			if (listener != null) {
				listener.onAddConsole();
			}
		}
	}


	public void setOnAddConsoleListener(OnAddConsoleListener listener) {
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return console.size();
	}

	@Override
	public ConsoleData getItem(int position) {
		return console.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public interface OnAddConsoleListener {
		void onAddConsole();
	}
}
