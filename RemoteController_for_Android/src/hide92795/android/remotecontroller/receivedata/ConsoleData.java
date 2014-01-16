package hide92795.android.remotecontroller.receivedata;

import android.text.Html;
import android.text.Spanned;

public class ConsoleData extends ReceiveData {
	private static final long serialVersionUID = -4861947434012112651L;
	private String date;
	private String log_level;
	private Spanned text;

	public Spanned getText() {
		return text;
	}

	public void setText(String text) {
		this.text = Html.fromHtml(text);
	}

	public String getLogLevel() {
		return log_level;
	}

	public void setLogLevel(String level) {
		this.log_level = level;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
