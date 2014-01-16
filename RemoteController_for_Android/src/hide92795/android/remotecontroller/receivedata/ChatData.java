package hide92795.android.remotecontroller.receivedata;

import android.text.Html;
import android.text.Spanned;

public class ChatData extends ReceiveData {
	private static final long serialVersionUID = -4197915736098155057L;
	private Spanned message;

	public Spanned getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = Html.fromHtml(message);
	}
}
