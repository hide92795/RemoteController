package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.command.Command;
import hide92795.bukkit.plugin.remotecontroller.command.CommandAuthentication;
import hide92795.bukkit.plugin.remotecontroller.command.CommandBan;
import hide92795.bukkit.plugin.remotecontroller.command.CommandChat;
import hide92795.bukkit.plugin.remotecontroller.command.CommandChatLog;
import hide92795.bukkit.plugin.remotecontroller.command.CommandConsoleCommand;
import hide92795.bukkit.plugin.remotecontroller.command.CommandConsoleLog;
import hide92795.bukkit.plugin.remotecontroller.command.CommandDirectory;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileDelete;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileEdit;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileOpen;
import hide92795.bukkit.plugin.remotecontroller.command.CommandFileRename;
import hide92795.bukkit.plugin.remotecontroller.command.CommandGamemode;
import hide92795.bukkit.plugin.remotecontroller.command.CommandGive;
import hide92795.bukkit.plugin.remotecontroller.command.CommandKick;
import hide92795.bukkit.plugin.remotecontroller.command.CommandMk;
import hide92795.bukkit.plugin.remotecontroller.command.CommandPlayers;
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
	}
}
