package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.command.Command;
import hide92795.bukkit.plugin.remotecontroller.command.CommandAuthentication;
import hide92795.bukkit.plugin.remotecontroller.command.CommandBan;
import hide92795.bukkit.plugin.remotecontroller.command.CommandCharSet;
import hide92795.bukkit.plugin.remotecontroller.command.CommandChat;
import hide92795.bukkit.plugin.remotecontroller.command.CommandChatLog;
import hide92795.bukkit.plugin.remotecontroller.command.CommandConsoleCommand;
import hide92795.bukkit.plugin.remotecontroller.command.CommandConsoleLog;
import hide92795.bukkit.plugin.remotecontroller.command.CommandDirectory;
import hide92795.bukkit.plugin.remotecontroller.command.CommandDynmap;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileDelete;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileEdit;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileOpen;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileRename;
import hide92795.bukkit.plugin.remotecontroller.command.CommandGamemode;
import hide92795.bukkit.plugin.remotecontroller.command.CommandGive;
import hide92795.bukkit.plugin.remotecontroller.command.CommandKick;
import hide92795.bukkit.plugin.remotecontroller.command.CommandMk;
import hide92795.bukkit.plugin.remotecontroller.command.CommandNotificationConsumeAll;
import hide92795.bukkit.plugin.remotecontroller.command.CommandNotificationLog;
import hide92795.bukkit.plugin.remotecontroller.command.CommandNotificationState;
import hide92795.bukkit.plugin.remotecontroller.command.CommandNotificationUnreadCount;
import hide92795.bukkit.plugin.remotecontroller.command.CommandPlayers;
import hide92795.bukkit.plugin.remotecontroller.command.CommandPluginInfo;
import hide92795.bukkit.plugin.remotecontroller.command.CommandPluginList;
import hide92795.bukkit.plugin.remotecontroller.command.CommandPluginState;
import hide92795.bukkit.plugin.remotecontroller.command.CommandServerIcon;
import hide92795.bukkit.plugin.remotecontroller.command.CommandServerInfo;
import java.util.HashMap;

public class Commands {
	public static final HashMap<String, Command> commands;
	static {
		commands = new HashMap<>();
		commands.put("AUTH", new CommandAuthentication());
		commands.put("SERVER_INFO", new CommandServerInfo());
		commands.put("CONSOLE_LOG", new CommandConsoleLog());
		commands.put("CHAT_LOG", new CommandChatLog());
		commands.put("CONSOLE_CMD", new CommandConsoleCommand());
		commands.put("PLAYERS", new CommandPlayers());
		commands.put("KICK", new CommandKick());
		commands.put("BAN", new CommandBan());
		commands.put("GIVE", new CommandGive());
		commands.put("GAMEMODE", new CommandGamemode());
		commands.put("DIRECTORY", new CommandDirectory());
		commands.put("FILE_OPEN", new CommandFileOpen());
		commands.put("FILE_RENAME", new CommandFileRename());
		commands.put("FILE_DELETE", new CommandFileDelete());
		commands.put("FILE_EDIT", new CommandFileEdit());
		commands.put("MK", new CommandMk());
		commands.put("CHAT", new CommandChat());
		commands.put("DYNMAP", new CommandDynmap());
		commands.put("PLUGIN_LIST", new CommandPluginList());
		commands.put("PLUGIN_STATE", new CommandPluginState());
		commands.put("PLUGIN_INFO", new CommandPluginInfo());
		commands.put("CHARSET", new CommandCharSet());
		commands.put("NOTIFICATION_LOG", new CommandNotificationLog());
		commands.put("NOTIFICATION_STATE", new CommandNotificationState());
		commands.put("NOTIFICATION_CONSUME_ALL", new CommandNotificationConsumeAll());
		commands.put("NOTIFICATION_UNREAD_COUNT", new CommandNotificationUnreadCount());
		commands.put("SERVER_ICON", new CommandServerIcon());
	}
}
