package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.receivedata.DirectoryData;
import hide92795.android.remotecontroller.receivedata.DirectoryData.File;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {
	private DirectoryData data;
	private LayoutInflater inflater;

	static class ViewHolder {
		ImageView icon;
		TextView name;
	}

	public FileListAdapter(Context context) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setDirectoryData(DirectoryData data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_editfile_object, null);
			ImageView icon = (ImageView) view.findViewById(R.id.image_editfile_icon);
			TextView name = (TextView) view.findViewById(R.id.text_editfile_name);

			holder = new ViewHolder();
			holder.icon = icon;
			holder.name = name;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		File item = getItem(position);

		if (item != null) {
			if (item.isDirectory()) {
				holder.icon.setImageResource(R.drawable.ic_folder);
			} else {
				holder.icon.setImageResource(R.drawable.ic_text_document);
			}
			holder.name.setText(item.getName());
			holder.name.setSelected(true);
		}
		return view;
	}

	@Override
	public int getCount() {
		if (data == null) {
			return 0;
		}
		return data.getFiles().length;
	}

	@Override
	public File getItem(int position) {
		try {
			return data.getFiles()[position];
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean isRootDirectory() {
		return data.isRoot();
	}
}
