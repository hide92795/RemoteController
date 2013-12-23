package hide92795.bukkit.plugin.remotecontroller.compatibility;

import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import org.apache.logging.log4j.LogManager;

public class LogRedirectWithLog4j {
	public static void regist(RemoteController plugin) throws Exception {
		org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
		logger.addAppender(new LogAppender(plugin));
	}
}
