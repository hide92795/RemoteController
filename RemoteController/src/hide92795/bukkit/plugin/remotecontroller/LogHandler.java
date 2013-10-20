package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.util.Util;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

	private RemoteController plugin;
	private SimpleDateFormat date;

	public LogHandler(RemoteController remotecontroller) {
		this.plugin = remotecontroller;
		this.date = new SimpleDateFormat("HH:mm:ss");
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		if (isLoggable(record)) {
			StringBuilder builder = new StringBuilder();
			Throwable ex = record.getThrown();

			builder.append(date.format(record.getMillis()));
			builder.append("-[");
			builder.append(record.getLevel().getLocalizedName().toUpperCase());
			builder.append("]-");
			builder.append(Util.convertColorCode(record.getMessage()));

			if (ex != null) {
				StringWriter writer = new StringWriter();
				ex.printStackTrace(new PrintWriter(writer));
				builder.append(writer);
			}

			plugin.onConsoleLogUpdate(builder.toString());
		}
	}
}
