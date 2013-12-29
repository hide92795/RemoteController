package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.receivedata.CommandData;
import hide92795.android.remotecontroller.receivedata.PermissionData;
import hide92795.android.remotecontroller.receivedata.PluginInfoBase;
import hide92795.android.remotecontroller.receivedata.PluginInfoData;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PluginInfoExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private PluginInfoBase[] datas;
	private LayoutInflater inflater;
	private final View detail;
	private final View permission_label;

	static class PluginInfoCommandViewHolder {
		TextView name;
		TextView alias;
		TextView description;
		ImageView indicator;
	}

	static class PluginInfoCommandDetailViewHolder {
		TextView permission;
		TextView usage;
	}

	static class PluginInfoPermissionViewHolder {
		TextView name;
		TextView description;
	}

	static class PluginInfoDetailViewData implements PluginInfoBase {
	}


	static class PluginInfoPermissionLabelData implements PluginInfoBase {
	}

	public PluginInfoExpandableListAdapter(Context context, PluginInfoData data) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.detail = inflater.inflate(R.layout.view_plugin_info_detail_object, null);
		this.permission_label = inflater.inflate(R.layout.view_plugin_info_permission_label_object, null);
		this.datas = createData(data);
		setDetailData(data);
	}

	private void setDetailData(PluginInfoData data) {
		TextView name = (TextView) detail.findViewById(R.id.text_plugin_info_detail_name);
		TextView description = (TextView) detail.findViewById(R.id.text_plugin_info_detail_description);
		TextView status = (TextView) detail.findViewById(R.id.text_plugin_info_detail_status);
		TextView version = (TextView) detail.findViewById(R.id.text_plugin_info_detail_version);
		TextView author = (TextView) detail.findViewById(R.id.text_plugin_info_detail_author);
		TextView web = (TextView) detail.findViewById(R.id.text_plugin_info_detail_web);

		name.setText(data.getName());
		description.setText(data.getDescription());
		version.setText(data.getVersion());
		author.setText(data.getAuthor());
		web.setText(data.getWeb());
		if (data.isEnabled()) {
			status.setText(R.string.str_enabled);
			status.setTextColor(context.getResources().getColor(R.color.color_enabled));
		} else {
			status.setText(R.string.str_disabled);
			status.setTextColor(context.getResources().getColor(R.color.red));
		}
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
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		PluginInfoBase base = getGroup(groupPosition);
		if (base instanceof CommandData) {
			PluginInfoCommandDetailViewHolder holder;
			if (view == null) {
				view = inflater.inflate(R.layout.view_plugin_info_command_detail_object, null);
				TextView permission = (TextView) view.findViewById(R.id.text_plugin_info_command_detail_permission);
				TextView usage = (TextView) view.findViewById(R.id.text_plugin_info_command_detail_usage);

				holder = new PluginInfoCommandDetailViewHolder();
				holder.permission = permission;
				holder.usage = usage;

				view.setTag(holder);
			} else {
				holder = (PluginInfoCommandDetailViewHolder) view.getTag();
			}

			CommandData data = (CommandData) base;

			if (data != null) {
				holder.permission.setText(data.getPermisson());
				holder.usage.setText(data.getUsage());
			}
		}
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (getGroup(groupPosition) instanceof CommandData) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public PluginInfoBase getGroup(int groupPosition) {
		return datas[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return datas.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean expanded, View view, ViewGroup parent) {
		PluginInfoBase base = getGroup(groupPosition);
		if (base instanceof PluginInfoDetailViewData) {
			view = detail;
		} else if (base instanceof CommandData) {
			PluginInfoCommandViewHolder holder;
			if (view == null) {
				view = createPluginInfoCommandView();
			} else {
				Object tag = view.getTag();
				if (!(tag instanceof PluginInfoCommandViewHolder)) {
					view = createPluginInfoCommandView();
				}
			}
			holder = (PluginInfoCommandViewHolder) view.getTag();

			CommandData data = (CommandData) base;

			if (data != null) {
				if (expanded) {
					holder.indicator.setImageResource(R.drawable.expanded);
				} else {
					holder.indicator.setImageResource(R.drawable.collapsed);
				}

				holder.name.setText(data.getCommand());
				holder.alias.setText(data.getAliases());
				holder.description.setText(data.getDescription());
			}
		} else if (base instanceof PluginInfoPermissionLabelData) {
			view = permission_label;
		} else if (base instanceof PermissionData) {
			PluginInfoPermissionViewHolder holder;
			if (view == null) {
				view = createPluginInfoPermissionView();
			} else {
				Object tag = view.getTag();
				if (!(tag instanceof PluginInfoPermissionViewHolder)) {
					view = createPluginInfoPermissionView();
				}
			}
			holder = (PluginInfoPermissionViewHolder) view.getTag();

			PermissionData data = (PermissionData) base;

			if (data != null) {
				holder.name.setText(data.getPermission());
				holder.description.setText(data.getDescription());
			}
		}
		return view;
	}

	private View createPluginInfoPermissionView() {
		View view = inflater.inflate(R.layout.view_plugin_info_permission_object, null);
		TextView name = (TextView) view.findViewById(R.id.text_plugin_info_permission_permission);
		TextView description = (TextView) view.findViewById(R.id.text_plugin_info_permission_description);

		PluginInfoPermissionViewHolder holder = new PluginInfoPermissionViewHolder();
		holder.name = name;
		holder.description = description;

		view.setTag(holder);
		return view;
	}

	private View createPluginInfoCommandView() {
		View view = inflater.inflate(R.layout.view_plugin_info_command_object, null);
		TextView name = (TextView) view.findViewById(R.id.text_plugin_info_command_command);
		TextView alias = (TextView) view.findViewById(R.id.text_plugin_info_command_alias);
		TextView description = (TextView) view.findViewById(R.id.text_plugin_info_command_desc);
		ImageView indicator = (ImageView) view.findViewById(R.id.image_plugin_info_command_indicator);

		PluginInfoCommandViewHolder holder = new PluginInfoCommandViewHolder();
		holder.name = name;
		holder.alias = alias;
		holder.description = description;
		holder.indicator = indicator;

		view.setTag(holder);
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

	private PluginInfoBase[] createData(PluginInfoData data) {
		ArrayList<PluginInfoBase> list = new ArrayList<PluginInfoBase>();
		list.add(new PluginInfoDetailViewData());
		for (CommandData command : data.getCommands()) {
			list.add(command);
		}
		list.add(new PluginInfoPermissionLabelData());
		for (PermissionData permission : data.getPermissions()) {
			list.add(permission);
		}
		return list.toArray(new PluginInfoBase[0]);
	}

}