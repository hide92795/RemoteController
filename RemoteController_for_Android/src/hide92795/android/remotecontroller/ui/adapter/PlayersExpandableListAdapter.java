package hide92795.android.remotecontroller.ui.adapter;

import hide92795.android.remotecontroller.PlayerFaceManager;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlayersExpandableListAdapter extends BaseExpandableListAdapter implements OnClickListener {
	private final Context context;
	private ArrayList<String> users;
	private LayoutInflater inflater;
	private OnPlayerHandleClickListener listener;

	static class PlayersViewHolder {
		ImageView face;
		TextView username;
		ProgressBar loading;
		ImageView indicator;
	}

	static class PlayerHandleViewHolder {
		Button kick;
		Button ban;
		Button give;
		Button mode;
	}

	public interface OnPlayerHandleClickListener {
		void onPlayerHandleClick(String username, int handle_id);
	}


	public PlayersExpandableListAdapter(Context context) {
		this.context = context;
		this.users = new ArrayList<String>();
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
		PlayerHandleViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_player_handle_object, null);
			Button kick = (Button) view.findViewById(R.id.btn_player_handle_kick);
			Button ban = (Button) view.findViewById(R.id.btn_player_handle_ban);
			Button give = (Button) view.findViewById(R.id.btn_player_handle_give);
			Button mode = (Button) view.findViewById(R.id.btn_player_handle_gamemode);

			holder = new PlayerHandleViewHolder();
			holder.kick = kick;
			holder.ban = ban;
			holder.give = give;
			holder.mode = mode;

			view.setTag(holder);
		} else {
			holder = (PlayerHandleViewHolder) view.getTag();
		}

		String username = users.get(position);

		if (username != null) {
			holder.kick.setTag(username);
			holder.ban.setTag(username);
			holder.give.setTag(username);
			holder.mode.setTag(username);
			holder.kick.setOnClickListener(this);
			holder.ban.setOnClickListener(this);
			holder.give.setOnClickListener(this);
			holder.mode.setOnClickListener(this);
		}
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return users.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return users.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int position, boolean expanded, View view, ViewGroup parent) {
		PlayersViewHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.view_player_object, null);
			ImageView face = (ImageView) view.findViewById(R.id.image_player_face);
			TextView username = (TextView) view.findViewById(R.id.text_player_username);
			ProgressBar loading = (ProgressBar) view.findViewById(R.id.progress_player_loading);
			ImageView indicator = (ImageView) view.findViewById(R.id.image_player_indicator);

			holder = new PlayersViewHolder();
			holder.face = face;
			holder.username = username;
			holder.loading = loading;
			holder.indicator = indicator;

			view.setTag(holder);
		} else {
			holder = (PlayersViewHolder) view.getTag();
		}

		String username = users.get(position);

		if (username != null) {
			PlayerFaceManager manager = ((Session) context.getApplicationContext()).getFaceManager();
			Bitmap face = manager.getFace(username);
			if (face != null) {
				holder.face.setImageBitmap(face);
				holder.loading.setVisibility(View.GONE);
			} else {
				holder.face.setImageResource(R.drawable.steve);
				holder.loading.setVisibility(View.VISIBLE);
			}

			if (expanded) {
				holder.indicator.setImageResource(R.drawable.expanded);
			} else {
				holder.indicator.setImageResource(R.drawable.collapsed);
			}
			// Log.d(Gravity.CENTER_VERTICAL + "", "" + holder.username.getGravity());

			holder.username.setText(username);
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

	public void add(String username) {
		users.add(username);
	}

	public void addAll(String... usernames) {
		for (String username : usernames) {
			add(username);
		}
	}

	@Override
	public void onClick(View v) {
		if (listener != null) {
			listener.onPlayerHandleClick((String) v.getTag(), v.getId());
		}
	}

	public void setOnPlayerHandleClickListener(OnPlayerHandleClickListener listener) {
		this.listener = listener;
	}

	public void clear() {
		users.clear();
		notifyDataSetChanged();
	}
}
