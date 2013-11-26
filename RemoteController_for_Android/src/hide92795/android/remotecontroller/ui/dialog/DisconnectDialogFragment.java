package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DisconnectDialogFragment extends DialogFragment {
	public static final int DISCONNECT_BY_NETWORK_REASON = 0;
	public static final int DISCONNECT_BY_SERVER = 1;
	public static final int DISCONNECT_BY_OWN = 2;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle data = getArguments();
		int mode = data.getInt("MODE");
		String reason = data.getString("REASON");

		Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.info_app_name);
		builder.setPositiveButton("OK", null);
		switch (mode) {
		case DISCONNECT_BY_NETWORK_REASON:
		case DISCONNECT_BY_SERVER:
			builder.setMessage(getString(R.string.str_disconnect_by_network_reason, reason));
			break;
		case DISCONNECT_BY_OWN:
			builder.setMessage(getString(R.string.str_disconnect_by_own));
			break;
		default:
			break;
		}
		AlertDialog dialog = builder.create();
		return dialog;
	}
}
