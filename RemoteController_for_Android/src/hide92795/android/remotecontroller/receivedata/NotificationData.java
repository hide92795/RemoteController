package hide92795.android.remotecontroller.receivedata;

import hide92795.android.remotecontroller.R;
import android.text.Html;
import android.text.Spanned;

public class NotificationData extends ReceiveData {
	private static enum Type {
		UNKNOWN(R.string.str_unknown), SUMMON(R.string.str_summons), CONSOLE_ERROR(R.string.str_console_error), CONSOLE_EXCEPTION(R.string.str_exception);

		private final int string_id;

		private Type(int string_id) {
			this.string_id = string_id;
		}
	}
	private static final long serialVersionUID = -4746511118019005214L;
	private String uuid;
	private int string_id;
	private String date;
	private Spanned message;
	private boolean consumed;

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public int getTypeStringID() {
		return string_id;
	}

	public void setType(String type) {
		Type t;
		try {
			t = Type.valueOf(type);
		} catch (Exception e) {
			t = Type.UNKNOWN;
		}
		this.string_id = t.string_id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Spanned getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = Html.fromHtml(message);
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}
}
