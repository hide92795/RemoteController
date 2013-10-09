package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FileCloseConfirmationDialogFragment extends DialogFragment {
	public static final int BUTTON_YES = 0;
	public static final int BUTTON_NO = 1;
	public static final int BUTTON_CANCEL = 2;

	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.str_confirm);
		builder.setMessage(R.string.str_file_close_confirm_message);
		builder.setPositiveButton(R.string.str_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				callback.onDialogClose(BUTTON_YES);
			}
		});
		builder.setNeutralButton(R.string.str_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				callback.onDialogClose(BUTTON_NO);
			}
		});
		builder.setNegativeButton(R.string.str_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				callback.onDialogClose(BUTTON_CANCEL);
			}
		});
		return builder.create();
	}
	public interface Callback {
		void onDialogClose(int result);
	}
}
