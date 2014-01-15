package hide92795.bukkit.plugin.remotecontroller.notification;

import java.nio.charset.Charset;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ConsoleException extends Notification {
	private final String log;

	public ConsoleException(String log) {
		this.log = log;
	}

	@Override
	public String toString() {
		String data_encoded = new String(Base64Coder.encode(log.getBytes(Charset.forName("UTF-8"))));
		StringBuilder sb = new StringBuilder();
		sb.append(getUUID());
		sb.append("-CONSOLE_EXCEPTION-");
		sb.append(getDate());
		sb.append("-");
		sb.append(data_encoded);
		if (!isConsumed()) {
			sb.append("-!");
		}
		return sb.toString();
	}
}
