package hide92795.bukkit.plugin.remotecontroller.compatibility;

import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import hide92795.bukkit.plugin.remotecontroller.util.Util;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

/**
 * This class is only for Bukkit 1.7.2 and later.
 */
public class LogAppender extends AbstractAppender {
	private RemoteController plugin;
	private SimpleDateFormat date;

	public LogAppender(RemoteController remotecontroller) {
		super("RemoteController", null, null);
		this.plugin = remotecontroller;
		this.date = new SimpleDateFormat("HH:mm:ss");
		start();
	}

	@Override
	public void append(LogEvent event) {
		StringBuilder builder = new StringBuilder();
		Throwable ex = event.getThrown();

		builder.append(date.format(event.getMillis()));
		builder.append("-[");
		builder.append(event.getLevel().name());
		builder.append("]-");
		builder.append(Util.convertColorCode(event.getMessage().getFormattedMessage()));

		if (ex != null) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			builder.append(writer);
		}

		plugin.onConsoleLogUpdate(builder.toString());

	}
}
