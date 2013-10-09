package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.Items;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Items.ItemData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemSelectListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ItemData[] items;

	static class ViewHolder {
		TextView localized_name;
		TextView original_name;
		TextView item_id;
		ImageView icon;
	}

	public ItemSelectListAdapter(Context context) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = Items.items;
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public ItemData getItem(int position) {
		if (position > items.length) {
			return null;
		}
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_item_select_object, null);
			TextView localized_name = (TextView) view.findViewById(R.id.text_item_select_localized_name);
			TextView original_name = (TextView) view.findViewById(R.id.text_item_select_original_name);
			TextView item_id = (TextView) view.findViewById(R.id.text_item_select_item_id);
			ImageView icon = (ImageView) view.findViewById(R.id.image_item_select_icon);

			holder = new ViewHolder();
			holder.localized_name = localized_name;
			holder.original_name = original_name;
			holder.item_id = item_id;
			holder.icon = icon;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ItemData item = this.getItem(position);

		if (item != null) {
			holder.localized_name.setText(item.localized_name_string_id);
			holder.localized_name.setSelected(true);
			holder.original_name.setText(item.default_name_string_id);
			holder.original_name.setSelected(true);
			holder.item_id.setText(item.item_id);
			holder.icon.setImageResource(item.icon_id);
		}
		return view;
	}

	public void setItems(ItemData[] items) {
		this.items = items;
		notifyDataSetChanged();
	}
}
