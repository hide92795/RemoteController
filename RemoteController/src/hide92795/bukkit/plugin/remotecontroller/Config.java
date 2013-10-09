package hide92795.bukkit.plugin.remotecontroller;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	public final int port;
	public final int log_max;
	public final int chat_max;
	public final List<String> editable_extension;

	public Config(FileConfiguration config) {
		this.port = config.getInt("Port");
		this.log_max = config.getInt("MaxLog");
		this.chat_max = config.getInt("MaxChat");
		this.editable_extension = config.getStringList("EditableExtension");
	}
}
