package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.ConnectionData;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AccountDeleteDialogFragment extends DialogFragment {
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}
		final int position = getArguments().getInt("POSITION");
		ConnectionData data = ((Session) getActivity().getApplication()).getSavedConnection().get(position);

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(data.getAddress() + ":" + data.getPort());
		builder.setMessage(R.string.str_confirm_delete_account);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(R.string.str_yes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int select) {
				callback.onAccountDelete(position);
			}
		});
		builder.setNegativeButton(R.string.str_no, null);
		return builder.create();
	}

	public interface Callback {
		void onAccountDelete(int position);
	}
}
