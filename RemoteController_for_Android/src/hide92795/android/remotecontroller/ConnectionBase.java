package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.receivedata.FileData;
import hide92795.android.remotecontroller.util.StringUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import org.java_websocket.client.WebSocketClient;

public abstract class ConnectionBase {
	public final Requests requests;
	protected final ConnectionData connection_data;
	protected AtomicBoolean auth = new AtomicBoolean(false);
	protected byte[] key;
	protected WebSocketClient socket;
	private int pid = 1;

	public ConnectionBase(ConnectionData connection_data) {
		this.requests = new Requests();
		this.connection_data = connection_data;
	}

	public abstract void start();

	protected abstract void send(String cmd, int pid, String text);

	protected abstract void sendData(String data);

	protected abstract void receive(String data);

	public abstract void close();

	public String getServerAddress() {
		return connection_data.getAddress();
	}

	public int getServerPort() {
		return connection_data.getPort();
	}

	public void authorize() {
		this.auth.set(true);
	}

	public int nextPid() {
		pid++;
		return pid;
	}

	@Override
	public String toString() {
		return getServerAddress() + ":" + getServerPort();
	}

	public class Requests {
		public void sendAuthorizeData() {
			send("AUTH", 0, StringUtils.join(":", connection_data.getUsername(), connection_data.getPassword()));
		}

		public int requestServerInfo() {
			int pid = nextPid();
			send("SERVER_INFO", pid, "");
			return pid;
		}

		public void startReceiveConsoleLog() {
			send("CONSOLE_LOG", 0, "START");
		}

		public void sendConsoleCommand(String cmd) {
			send("CONSOLE_CMD", 0, cmd);
		}

		public int requestOnlinePlayers() {
			int pid = nextPid();
			send("PLAYERS", pid, "");
			return pid;
		}

		public int requestKick(String username, String reason) {
			int pid = nextPid();
			send("KICK", pid, StringUtils.join(":", username, reason));
			return pid;
		}

		public int requestBan(String username, String reason) {
			int pid = nextPid();
			send("BAN", pid, StringUtils.join(":", username, reason));
			return pid;
		}

		public int requestGive(String username, String item, int num) {
			int pid = nextPid();
			send("GIVE", pid, StringUtils.join(":", username, num, item));
			return pid;
		}

		public int requestGamemode(String username, int mode) {
			int pid = nextPid();
			send("GAMEMODE", pid, StringUtils.join(":", username, mode));
			return pid;
		}

		public void startReceiveChatLog() {
			send("CHAT_LOG", 0, "START");
		}

		public int requestDirectory(String dir) {
			int pid = nextPid();
			send("DIRECTORY", pid, dir);
			return pid;
		}

		public int requestFileOpen(String file, String encoding) {
			int pid = nextPid();
			send("FILE_OPEN", pid, StringUtils.join(":", file, encoding));
			return pid;
		}

		public int requestFileRename(String path, String new_name) {
			int pid = nextPid();
			send("FILE_RENAME", pid, StringUtils.join(":", path, new_name));
			return pid;
		}

		public int requestFileDelete(String path) {
			int pid = nextPid();
			send("FILE_DELETE", pid, path);
			return pid;
		}

		public int requestFileEdit(FileData data) {
			int pid = nextPid();
			send("FILE_EDIT", pid, data.toString());
			return pid;
		}

		public int requestMk(String path) {
			int pid = nextPid();
			send("MK", pid, path);
			return pid;
		}

		public int requestChat(String chat) {
			int pid = nextPid();
			send("CHAT", pid, chat);
			return pid;
		}

		public int requestDynmap() {
			int pid = nextPid();
			send("DYNMAP", pid, "");
			return pid;
		}

		public int requestPluginList() {
			int pid = nextPid();
			send("PLUGIN_LIST", pid, "");
			return pid;
		}

		public int requestChangePluginState(String name, boolean enable) {
			int pid = nextPid();
			send("PLUGIN_STATE", pid, StringUtils.join(":", Boolean.toString(enable), name));
			return pid;
		}

		public int requestPluginInfo(String name) {
			int pid = nextPid();
			send("PLUGIN_INFO", pid, name);
			return pid;
		}

		public void requestCharSet() {
			send("CHARSET", 0, "START");
		}

		public void startReceiveNotificationLog() {
			send("NOTIFICATION_LOG", 0, "START");
		}

		public void requestChangeNotificationConsumeState(String uuid, boolean consumed) {
			send("NOTIFICATION_STATE", 0, StringUtils.join(":", uuid, Boolean.toString(consumed)));
		}

		public void requestConsumeAllNotifications() {
			send("NOTIFICATION_CONSUME_ALL", 0, "START");
		}

		public int requestNotificationUnreadCount() {
			int pid = nextPid();
			send("NOTIFICATION_UNREAD_COUNT", pid, "");
			return pid;
		}

		public int requestServerIcon() {
			int pid = nextPid();
			send("SERVER_ICON", pid, "");
			return pid;
		}
	}
}
