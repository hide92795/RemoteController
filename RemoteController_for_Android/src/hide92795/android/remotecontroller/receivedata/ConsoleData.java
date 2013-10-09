package hide92795.android.remotecontroller.receivedata;

public class ConsoleData extends ReceiveData {
	private String date;
	private String log_level;
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
