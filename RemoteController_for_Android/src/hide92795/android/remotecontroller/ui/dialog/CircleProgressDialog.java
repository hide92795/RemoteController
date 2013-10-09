package hide92795.android.remotecontroller.ui.dialog;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.util.LogUtil;
import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;

public class CircleProgressDialog extends Dialog {
	public CircleProgressDialog(Context context) {
		super(context, R.style.Theme_CircleProgressDialog);
		setContentView(R.layout.circle_progress_dialog);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.d("", "Dialog closed");
	}
}