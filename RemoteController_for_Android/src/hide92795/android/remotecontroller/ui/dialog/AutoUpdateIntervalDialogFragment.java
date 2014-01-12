package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AutoUpdateIntervalDialogFragment extends DialogFragment {
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.str_update_interval);
		int itemid;
		if (Session.isDebug()) {

		}



		return builder.create();
	}

	public interface Callback {
		void onAccountDelete(int position);
	}

	// public enum
}
