package hide92795.bukkit.plugin.remotecontroller.notification;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Notification {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private final String date;
	private String uuid;
	private boolean consumed = false;

	public Notification() {
		this.date = DATE_FORMAT.format(new Date());
	}

	public String getDate() {
		return date;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}

	public abstract String toString();
}
