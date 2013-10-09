package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.receivedata.DirectoryData.File;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FileHandleDialogFragment extends DialogFragment {
	public static final int HANDLE_OPEN = 0;
	public static final int HANDLE_OPEN_SELECT_CHARSET = 1;
	public static final int HANDLE_RENAME = 2;
	public static final int HANDLE_DELETE = 3;
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}
		final File file = getArguments().getParcelable("FILE");

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(file.getName());
		if (file.isDirectory()) {
			builder.setItems(R.array.strarr_file_handle_directory, new OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int select) {
					switch (select) {
					case 0:
						callback.onDialogSelected(file, HANDLE_RENAME);
						break;
					case 1:
						callback.onDialogSelected(file, HANDLE_DELETE);
						break;
					default:
						break;
					}
				}
			});
		} else {
			builder.setItems(R.array.strarr_file_handle_file, new OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int select) {
					switch (select) {
					case 0:
						callback.onDialogSelected(file, HANDLE_OPEN);
						break;
					case 1:
						callback.onDialogSelected(file, HANDLE_OPEN_SELECT_CHARSET);
						break;
					case 2:
						callback.onDialogSelected(file, HANDLE_RENAME);
						break;
					case 3:
						callback.onDialogSelected(file, HANDLE_DELETE);
						break;
					default:
						break;
					}
				}
			});
		}
		return builder.create();
	}
	public interface Callback {
		void onDialogSelected(File file, int result);
	}
}
