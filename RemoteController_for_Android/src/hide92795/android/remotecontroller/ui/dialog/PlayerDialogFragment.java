package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.activity.ItemSelectActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlayerDialogFragment extends DialogFragment {
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final String username = getArguments().getString("USERNAME");
		int handle_id = getArguments().getInt("HANDLE_ID");

		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}
		Builder builder = new AlertDialog.Builder(getActivity());
		switch (handle_id) {
		case R.id.btn_player_handle_kick: {
			builder.setTitle(R.string.str_kick);
			builder.setMessage(getActivity().getString(R.string.str_confirm_kick, username));
			final EditText edittext = new EditText(getActivity());
			edittext.setHint(R.string.str_reason);
			builder.setView(edittext);
			builder.setPositiveButton(R.string.str_yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Bundle data = new Bundle();
					data.putString("REASON", edittext.getText().toString());
					callback.onDialogClicked(username, R.id.btn_player_handle_kick, data);
				}
			});
			builder.setNegativeButton(R.string.str_no, null);
			break;
		}
		case R.id.btn_player_handle_ban: {
			builder.setTitle(R.string.str_ban);
			builder.setMessage(getActivity().getString(R.string.str_confirm_ban, username));
			final EditText edittext = new EditText(getActivity());
			edittext.setHint(R.string.str_reason);
			builder.setView(edittext);
			builder.setPositiveButton(R.string.str_yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Bundle data = new Bundle();
					data.putString("REASON", edittext.getText().toString());
					callback.onDialogClicked(username, R.id.btn_player_handle_ban, data);
				}
			});
			builder.setNegativeButton(R.string.str_no, null);
			break;
		}
		case R.id.btn_player_handle_give: {
			builder.setTitle(getActivity().getString(R.string.str_give));
			builder.setMessage(R.string.str_select_give_item);
			LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
			View view = inflater.inflate(R.layout.view_player_give, null);
			Button select_item = (Button) view.findViewById(R.id.btn_player_give_item_select);
			select_item.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), ItemSelectActivity.class);
					startActivityForResult(i, 1);
				}
			});
			final EditText edittext_item = (EditText) view.findViewById(R.id.edittext_player_give_item);
			edittext_item.setFilters(new InputFilter[] { new ItemIDFilter() });
			final EditText edittext_num = (EditText) view.findViewById(R.id.edittext_player_give_num);
			builder.setView(view);
			builder.setPositiveButton(R.string.str_yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Bundle data = new Bundle();
					data.putString("ITEM", edittext_item.getText().toString());
					data.putString("NUM", edittext_num.getText().toString());
					callback.onDialogClicked(username, R.id.btn_player_handle_give, data);
				}
			});
			builder.setNegativeButton(R.string.str_no, null);
			break;
		}
		case R.id.btn_player_handle_gamemode: {
			builder.setTitle(getActivity().getString(R.string.str_gamemode));
			builder.setItems(R.array.strarr_gamemode, new OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int select) {
					Bundle data = new Bundle();
					data.putInt("GAMEMODE", select);
					callback.onDialogClicked(username, R.id.btn_player_handle_gamemode, data);
				}
			});
			break;
		}
		default:
			break;
		}
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		return dialog;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle return_data = data.getExtras();
				String item_id = return_data.getString("ITEM_ID");
				if (item_id != null) {
					EditText item = (EditText) getDialog().findViewById(R.id.edittext_player_give_item);
					item.setText(item_id);
				}
			}
		}
	}


	public interface Callback {
		void onDialogClicked(String username, int handle_id, Bundle data);
	}

	private class ItemIDFilter implements InputFilter {
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.toString().matches("^[0-9:]+$")) {
				return source;
			} else {
				return "";
			}
		}
	}
}
