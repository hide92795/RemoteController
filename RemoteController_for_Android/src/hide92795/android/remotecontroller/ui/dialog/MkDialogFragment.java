package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.util.FileNameFilter;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

public class MkDialogFragment extends DialogFragment {
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}

		final String current_directory = getArguments().getString("CURRENT");
		final boolean isDirectory = getArguments().getBoolean("DIRECTORY");
		final EditText edittext = new EditText((FragmentActivity) callback);
		edittext.setFilters(new InputFilter[] { new FileNameFilter() });
		edittext.setInputType(InputType.TYPE_CLASS_TEXT);

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.str_create);
		builder.setMessage(isDirectory ? R.string.str_input_directory_name : R.string.str_input_file_name);
		builder.setView(edittext);
		builder.setPositiveButton(R.string.str_ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int select) {
				String name = edittext.getText().toString().trim();
				if (name.length() != 0) {
					if (isDirectory) {
						name = name + "/";
					}
					callback.onMkSelected(current_directory + "/" + name);
				}
			}
		});
		builder.setNegativeButton(R.string.str_cancel, new OnClickListener() {
			public void onClick(DialogInterface dialog, int select) {
			}
		});
		return builder.create();
	}

	public interface Callback {
		void onMkSelected(String path);
	}
}
