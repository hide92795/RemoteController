package hide92795.bukkit.plugin.remotecontroller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	private SimpleDateFormat date;

	public LogFormatter() {
		date = new SimpleDateFormat("HH:mm:ss");
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		Throwable ex = record.getThrown();

		builder.append(date.format(record.getMillis()));
		builder.append(" [");
		builder.append(record.getLevel().getLocalizedName().toUpperCase());
		builder.append("] ");
		builder.append(formatMessage(record));
		builder.append('\n');

		if (ex != null) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			builder.append(writer);
		}

		return builder.toString();
	}

}
