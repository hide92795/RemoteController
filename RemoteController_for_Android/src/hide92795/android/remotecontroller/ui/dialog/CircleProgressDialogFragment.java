package hide92795.android.remotecontroller.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class CircleProgressDialogFragment extends DialogFragment {
	private OnCancelListener listener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		CircleProgressDialog dialog = new CircleProgressDialog(getActivity());
		return dialog;
	}

	public void setOnCancelListener(OnCancelListener listener) {
		this.listener = listener;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (listener != null) {
			listener.onCancel();
		}

	}
	public interface OnCancelListener {
		void onCancel();
	}
}
