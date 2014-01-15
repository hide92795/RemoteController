package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.notification.ConsoleError;
import hide92795.bukkit.plugin.remotecontroller.notification.ConsoleException;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

	private RemoteController plugin;
	private SimpleDateFormat date;

	public LogHandler(RemoteController remotecontroller) {
		this.plugin = remotecontroller;
		this.date = new SimpleDateFormat("HH:mm:ss");
		setFilter(new Filter() {
			@Override
			public boolean isLoggable(LogRecord record) {
				return true;
			}
		});
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		Throwable ex = record.getThrown();

		String message = Util.convertColorCode(record.getMessage());

		builder.append(date.format(record.getMillis()));
		builder.append("-[");
		builder.append(record.getLevel().getName());
		builder.append("]-");
		builder.append(message);

		if (ex != null) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			builder.append(writer);
			ConsoleException exception = new ConsoleException(ex.toString());
			plugin.onNotificationUpdate(exception);
		}

		if (record.getLevel().equals(Level.WARNING)) {
			if (plugin.config.notification_include_warn_log) {
				sendNotificationError(message);
			}
		} else if (record.getLevel().equals(Level.SEVERE)) {
			sendNotificationError(message);
		}

		plugin.onConsoleLogUpdate(builder.toString());
	}

	private void sendNotificationError(String message) {
		ConsoleError error = new ConsoleError(message);
		plugin.onNotificationUpdate(error);
	}
}
