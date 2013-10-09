package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import java.nio.charset.Charset;
import java.util.Set;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CharsetDialogFragment extends DialogFragment {
	private static String[] CHARSETS = createDefault();
	private Callback callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getTargetFragment() != null && getTargetFragment() instanceof Callback) {
			callback = (Callback) getTargetFragment();
		} else if (getActivity() != null && getActivity() instanceof Callback) {
			callback = (Callback) getActivity();
		}

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.str_charset);
		builder.setItems(CHARSETS, new OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface, int position) {
				callback.onCharsetSelected(CHARSETS[position], getArguments());
			}
		});
		return builder.create();
	}

	public interface Callback {
		void onCharsetSelected(String charset, Bundle arg);
	}

	public static void setCharsets(String[] charsets) {
		CHARSETS = charsets;
	}

	private static String[] createDefault() {
		Set<String> set = Charset.availableCharsets().keySet();
		return set.toArray(new String[set.size()]);
	}
}
