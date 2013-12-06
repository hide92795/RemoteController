package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.receivedata.PluginListData.PluginData;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PluginExpandableListAdapter extends BaseExpandableListAdapter implements OnClickListener {
	private Context context;
	private ArrayList<PluginData> plugins;
	private LayoutInflater inflater;
	private OnPluginHandleClickListener listener;

	static class PluginViewHolder {
		TextView name;
		TextView status;
		ImageView indicator;
	}

	static class PluginHandleViewHolder {
		Button view;
		Button enable;
		Button disable;
	}

	public interface OnPluginHandleClickListener {
		void onPluginHandleClick(PluginData pluginData, int handle_id);
	}

	public PluginExpandableListAdapter(Context context) {
		this.context = context;
		this.plugins = new ArrayList<PluginData>();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int position, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		PluginHandleViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_plugin_handle_object, null);
			Button btn_view = (Button) view.findViewById(R.id.btn_plugin_handle_view);
			Button btn_enable = (Button) view.findViewById(R.id.btn_plugin_handle_enable);
			Button btn_disable = (Button) view.findViewById(R.id.btn_plugin_handle_disable);

			holder = new PluginHandleViewHolder();
			holder.view = btn_view;
			holder.enable = btn_enable;
			holder.disable = btn_disable;

			view.setTag(holder);
		} else {
			holder = (PluginHandleViewHolder) view.getTag();
		}

		PluginData plugindata = plugins.get(position);

		if (plugindata != null) {
			holder.view.setTag(plugindata);
			holder.enable.setTag(plugindata);
			holder.disable.setTag(plugindata);
			holder.view.setOnClickListener(this);
			holder.enable.setOnClickListener(this);
			holder.disable.setOnClickListener(this);

			if (plugindata.enable) {
				holder.enable.setVisibility(View.INVISIBLE);
				holder.disable.setVisibility(View.VISIBLE);
			} else {
				holder.enable.setVisibility(View.VISIBLE);
				holder.disable.setVisibility(View.INVISIBLE);
			}
		}
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public PluginData getGroup(int groupPosition) {
		return plugins.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return plugins.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int position, boolean expanded, View view, ViewGroup parent) {
		PluginViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_plugin_object, null);
			TextView name = (TextView) view.findViewById(R.id.text_plugin_name);
			TextView status = (TextView) view.findViewById(R.id.text_plugin_status);
			ImageView indicator = (ImageView) view.findViewById(R.id.image_plugin_indicator);

			holder = new PluginViewHolder();
			holder.name = name;
			holder.status = status;
			holder.indicator = indicator;

			view.setTag(holder);
		} else {
			holder = (PluginViewHolder) view.getTag();
		}

		PluginData data = plugins.get(position);

		if (data != null) {
			if (expanded) {
				holder.indicator.setImageResource(R.drawable.expanded);
			} else {
				holder.indicator.setImageResource(R.drawable.collapsed);
			}

			holder.name.setText(data.name);
			if (data.enable) {
				holder.status.setText(R.string.str_enabled);
				holder.status.setTextColor(context.getResources().getColor(R.color.color_enabled));
			} else {
				holder.status.setText(R.string.str_disabled);
				holder.status.setTextColor(context.getResources().getColor(R.color.red));
			}
		}
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void add(PluginData data) {
		plugins.add(data);
	}

	public void addAll(PluginData... plugins) {
		for (PluginData data : plugins) {
			add(data);
		}
	}

	@Override
	public void onClick(View v) {
		if (listener != null) {
			listener.onPluginHandleClick((PluginData) v.getTag(), v.getId());
		}
	}

	public void setOnPluginHandleClickListener(OnPluginHandleClickListener listener) {
		this.listener = listener;
	}

	public void clear() {
		plugins.clear();
		notifyDataSetChanged();
	}
}
