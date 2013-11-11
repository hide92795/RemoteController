package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NotRecommendedVersionServerDialogFragment extends DialogFragment {
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		} else {
			throw new RuntimeException("Caller activity must implement callback!");
		}
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.info_app_name);
		builder.setMessage(R.string.str_not_recommended_server_continue);
		builder.setPositiveButton(R.string.str_continue, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onDialogClicked(true);
			}
		});
		builder.setNegativeButton(R.string.str_disconnect, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onDialogClicked(false);
			}
		});
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		return dialog;
	}

	public interface Callback {
		void onDialogClicked(boolean select_continue);
	}
}
