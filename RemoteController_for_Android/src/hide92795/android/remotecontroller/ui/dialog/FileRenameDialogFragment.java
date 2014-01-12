package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.receivedata.DirectoryData.File;
import hide92795.android.remotecontroller.ui.dialog.AutoUpdateIntervalDialogFragment.Callback;
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

public class FileRenameDialogFragment extends DialogFragment implements Callback {
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}
		final File file = getArguments().getParcelable("FILE");
		final EditText edittext = new EditText((FragmentActivity) callback);
		edittext.setFilters(new InputFilter[] { new FileNameFilter() });
		edittext.setInputType(InputType.TYPE_CLASS_TEXT);
		edittext.setText(file.getName());

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.str_rename);
		builder.setMessage(R.string.str_input_new_name);
		builder.setView(edittext);
		builder.setPositiveButton(R.string.str_ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int select) {
				callback.onFileRenameSelected(file, edittext.getText().toString());
			}
		});
		builder.setNegativeButton(R.string.str_cancel, new OnClickListener() {
			public void onClick(DialogInterface dialog, int select) {
			}
		});

		return builder.create();
	}

	public interface Callback {
		void onFileRenameSelected(File file, String new_name);
	}

	@Override
	public void onAccountDelete(int position) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
