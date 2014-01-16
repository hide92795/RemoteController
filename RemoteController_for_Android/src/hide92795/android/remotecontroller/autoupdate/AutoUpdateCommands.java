package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommand;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommandAuthentication;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommandError;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommandNotification;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommandNotificationUnreadCount;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommandRequestAuthentication;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommandServerIcon;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommandServerInfo;
import java.util.HashMap;

public class AutoUpdateCommands {
	public static final HashMap<String, AutoUpdateCommand> commands;
	static {
		commands = new HashMap<String, AutoUpdateCommand>();
		commands.put("REQUEST_AUTH", new AutoUpdateCommandRequestAuthentication());
		commands.put("AUTH", new AutoUpdateCommandAuthentication());
		commands.put("SERVER_INFO", new AutoUpdateCommandServerInfo());
		commands.put("NOTIFICATION", new AutoUpdateCommandNotification());
		commands.put("NOTIFICATION_UNREAD_COUNT", new AutoUpdateCommandNotificationUnreadCount());
		commands.put("SERVER_ICON", new AutoUpdateCommandServerIcon());
		commands.put("ERROR", new AutoUpdateCommandError());
	}
}
