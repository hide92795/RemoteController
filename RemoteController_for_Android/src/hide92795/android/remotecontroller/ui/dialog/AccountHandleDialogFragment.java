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

public class AccountHandleDialogFragment extends DialogFragment {
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}
		final int position = getArguments().getInt("POSITION");
		String uuid = ((Session) getActivity().getApplication()).getSavedConnection().getIds().get(position);
		ConnectionData data = ((Session) getActivity().getApplication()).getSavedConnection().getDatas().get(uuid);

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(data.getAddress() + ":" + data.getPort());
		builder.setItems(R.array.strarr_account_manager_handle, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onAccountHandled(position, which);
			}
		});
		return builder.create();
	}

	public interface Callback {
		void onAccountHandled(int position, int handle);
	}
}
