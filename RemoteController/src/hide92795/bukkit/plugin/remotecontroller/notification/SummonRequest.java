package hide92795.bukkit.plugin.remotecontroller.notification;

import java.nio.charset.Charset;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class SummonRequest extends Notification {
	private final String sender;
	private final String message;

	public SummonRequest(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	@Override
	public String toString() {
		String data = sender + ":" + message;
		String data_encoded = new String(Base64Coder.encode(data.getBytes(Charset.forName("UTF-8"))));
		StringBuilder sb = new StringBuilder();
		sb.append(getUUID());
		sb.append("-SUMMON-");
		sb.append(getDate());
		sb.append("-");
		sb.append(data_encoded);
		if (!isConsumed()) {
			sb.append("-!");
		}
		return sb.toString();
	}
}
