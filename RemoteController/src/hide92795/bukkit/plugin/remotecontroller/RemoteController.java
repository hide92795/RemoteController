package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.corelib.Localize;
import hide92795.bukkit.plugin.corelib.Usage;
import hide92795.bukkit.plugin.remotecontroller.api.AdditionalInfo;
import hide92795.bukkit.plugin.remotecontroller.api.AdditionalInfoCreator;
import hide92795.bukkit.plugin.remotecontroller.api.RemoteControllerAPI;
import hide92795.bukkit.plugin.remotecontroller.compatibility.LogRedirectWithLog4j;
import hide92795.bukkit.plugin.remotecontroller.listener.BroadcastListener;
import hide92795.bukkit.plugin.remotecontroller.listener.ChatListenerWithAsyncPlayerChatEvent;
import hide92795.bukkit.plugin.remotecontroller.listener.ChatListenerWithPlayerChatEvent;
import hide92795.bukkit.plugin.remotecontroller.notification.Notification;
import hide92795.bukkit.plugin.remotecontroller.notification.SummonRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


public class RemoteController extends JavaPlugin {
	public static RemoteController instance;
	private Logger logger;
	public Localize localize;
	private ConcurrentHashMap<String, String> users;
	public Config config;
	private RemoteServer server;
	private ConcurrentLinkedQueue<String> log;
	private ConcurrentLinkedQueue<String> chat;
	private NotificationManager notification;
	private ArrayList<AdditionalInfoCreator> additional_info_creators;
	private RemoteControllerAPI api;
	private Usage usage;
	private boolean chat_event_type_is_broadcast;

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		logger = getLogger();
		localize = new Localize(this);
		users = new ConcurrentHashMap<>();
		log = new ConcurrentLinkedQueue<>();
		chat = new ConcurrentLinkedQueue<>();
		notification = new NotificationManager(this);
		additional_info_creators = new ArrayList<>();
		api = new RemoteControllerAPI(this);
		try {
			reload();
			logger.info("Loaded config successfully.");
		} catch (Exception e1) {
			logger.severe("Error has occurred on loading config.");
		}

		startServer();

		startRedirectConsoleLog();

		startRedirectChatLog();

		logger.info("RemoteController enabled!");
	}

	private void startRedirectConsoleLog() {
		try {
			Class.forName("org.apache.logging.log4j.LogManager");
			LogRedirectWithLog4j.regist(this);
			logger.info("Start redirect console log with log4j.");
		} catch (NoClassDefFoundError | Exception e) {
			LogHandler handler = new LogHandler(this);
			handler.setLevel(Level.ALL);
			getServer().getLogger().addHandler(handler);
			logger.info("Start redirect console log with Java Logging API.");
		}
	}

	private void startRedirectChatLog() {
		try {
			Class.forName("org.bukkit.event.server.ServerBroadcastEvent");
			getServer().getPluginManager().registerEvents(new BroadcastListener(this), this);
			chat_event_type_is_broadcast = true;
			logger.info("Start redirect chat log with ServerBroadcastEvent.");
		} catch (NoClassDefFoundError | Exception e) {
			try {
				Class.forName("org.bukkit.event.player.AsyncPlayerChatEvent");
				getServer().getPluginManager().registerEvents(new ChatListenerWithAsyncPlayerChatEvent(this), this);
				chat_event_type_is_broadcast = false;
				logger.info("Start redirect chat log with AsyncPlayerChatEvent.");
			} catch (NoClassDefFoundError | Exception e2) {
				getServer().getPluginManager().registerEvents(new ChatListenerWithPlayerChatEvent(this), this);
				chat_event_type_is_broadcast = false;
				logger.info("Start redirect chat log with PlayerChatEvent.");
			}
		}
	}

	private void createUsage() {
		usage = new Usage(this);
		usage.addCommand("/remotecontroller-user add <" + localize.getString(Type.USERNAME) + "> <" + localize.getString(Type.PASSWORD) + ">", localize.getString(Type.USAGE_USER_ADD));
		usage.addCommand("/remotecontroller-user remove <" + localize.getString(Type.USERNAME) + ">", localize.getString(Type.USAGE_USER_REMOVE));
		usage.addCommand("/remotecontroller-user list", localize.getString(Type.USAGE_USER_LIST));
		usage.addCommand("/remotecontroller-reload", localize.getString(Type.USAGE_RELOAD_SETTING));
	}

	@Override
	public void onDisable() {
		stopServer();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName().toLowerCase()) {
		case "remotecontroller":
			sender.sendMessage(usage.toString());
			break;
		case "remotecontroller-user":
			if (args.length == 0) {
				sender.sendMessage(usage.toString());
				break;
			}
			String sub_cmd = args[0];
			switch (sub_cmd) {
			case "add":
				if (args.length == 3) {
					if (canAccessUserModifyCommand(sender)) {
						if (users.containsKey(args[1])) {
							sender.sendMessage(localize.getString(Type.USER_ALREADY_EXIST));
						} else {
							users.put(args[1], args[2]);
							sender.sendMessage(localize.getString(Type.USER_ADD_SUCCESS));
							saveUserData();
						}
					} else {
						sender.sendMessage("This command can only be accessed from console.");
					}
				} else {
					sender.sendMessage(usage.toString());
				}
				break;
			case "remove":
				if (args.length == 2) {
					if (canAccessUserModifyCommand(sender)) {
						if (users.containsKey(args[1])) {
							users.remove(args[1]);
							sender.sendMessage(localize.getString(Type.USER_REMOVE_SUCCESS));
							saveUserData();
						} else {
							sender.sendMessage(localize.getString(Type.USER_NOT_FOUND));
						}
					} else {
						sender.sendMessage("This command can only be accessed from console.");
					}
				} else {
					sender.sendMessage(usage.toString());
				}
				break;
			case "list":
				sendUserList(sender);
				break;
			default:
				break;
			}
			break;
		case "remotecontroller-reload":
			try {
				stopServer();
				reload();
				startServer();
				sender.sendMessage(localize.getString(Type.RELOADED_SETTING));
				logger.info("Reloaded successfully.");
			} catch (Exception e) {
				sender.sendMessage(localize.getString(Type.ERROR_RELOAD_SETTING));
			}
			break;
		case "remotecontroller-summon":
			StringBuilder sb = new StringBuilder();
			if (args.length == 1) {
				String sender_name = sender.getName();
				String message = args[0];
				SummonRequest request = new SummonRequest(sender_name, message);
				onNotificationUpdate(request);
				sb.append(localize.getString(Type.SENDED_SUMMON_REQUEST));
			} else {
				sb.append(ChatColor.GREEN);
				sb.append("/");
				sb.append(label);
				sb.append(" <");
				sb.append(localize.getString(Type.MESSAGE));
				sb.append(">\n");
				sb.append(localize.getString(Type.USAGE_SUMMON));
			}
			sender.sendMessage(sb.toString());
			break;
		default:
			break;
		}
		return true;
	}

	private boolean canAccessUserModifyCommand(CommandSender sender) {
		if (config.console_only) {
			if (sender.getName().equals(getServer().getConsoleSender().getName())) {
				return true;
			}
			return false;
		}
		return true;
	}

	private void sendUserList(CommandSender sender) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.YELLOW);
		sb.append("====================");
		sb.append("\n");
		sb.append("No  Name");
		sb.append("\n");
		sb.append(ChatColor.GREEN);
		Set<String> set = users.keySet();
		int i = 1;
		for (String name : set) {
			sb.append(i);
			sb.append("  ");
			sb.append(name);
			sb.append("\n");
			i++;
		}
		sb.append(ChatColor.YELLOW);
		sb.append("====================");
		sender.sendMessage(sb.toString());
	}

	private void reload() throws Exception {
		reloadConfig();
		String lang = getConfig().getString("Language");
		if (lang.equals("detect")) {
			lang = Locale.getDefault().getLanguage();
			getConfig().set("Language", lang);
			saveConfig();
		}
		try {
			localize.reload(lang, "en");
		} catch (Exception e1) {
			logger.severe("Can't load language file.");
			try {
				localize.reload("en");
				logger.severe("Loaded default language file.");
			} catch (Exception e) {
				throw e;
			}
		}
		loadUserData();
		config = new Config(this, getConfig());
		createUsage();
	}

	@SuppressWarnings("unchecked")
	private void loadUserData() {
		File userdata = new File(getDataFolder(), "users.dat");
		if (userdata.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userdata))) {
				users = (ConcurrentHashMap<String, String>) ois.readObject();
			} catch (Exception e) {
				logger.severe("An error has occurred loading userdata!");
				users = new ConcurrentHashMap<>();
				saveUserData();
				logger.severe("Created new file.");
			}
		} else {
			users = new ConcurrentHashMap<>();
			saveUserData();
		}
	}

	private void saveUserData() {
		File userdata = new File(getDataFolder(), "users.dat");
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userdata))) {
			oos.writeObject(users);
			oos.flush();
		} catch (Exception e) {
			logger.severe("An error has occurred saving userdata!");
		}
	}

	private void startServer() {
		getLogger().info("Starting remote server...");
		getLogger().info("This server is powered by Java-WebSocket.");
		getLogger().info("http://java-websocket.org/");
		server = new RemoteServer(this);
		server.start();
		getLogger().info("Started remote server successfully.");
		getLogger().info("Remote server port: " + this.config.port);
	}

	private void stopServer() {
		getLogger().info("Stopping remote server...");
		server.stopServer();
		getLogger().info("Stopped remote server successfully.");
	}

	public boolean checkUser(String username, String password) {
		if (users.containsKey(username)) {
			if (users.get(username).equals(password)) {
				return true;
			}
		}
		return false;
	}

	public void onConsoleLogUpdate(String message) {
		try {
			server.sendConsoleLog(message);
			addConsoleLog(message);
		} catch (Exception e) {
		}
	}

	public void onChatLogUpdate(String message) {
		server.sendChatLog(message);
		addChatLog(message);
	}

	public void onNotificationUpdate(Notification notification) {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "_");
		notification.setUUID(uuid);
		server.sendNotificationLog(notification);
		addNotification(uuid, notification);
	}

	private void addConsoleLog(String log_s) {
		log.add(log_s);
		if (log.size() > config.log_max) {
			log.poll();
		}
	}

	private void addChatLog(String chat_s) {
		chat.add(chat_s);
		if (chat.size() > config.chat_max) {
			chat.poll();
		}
	}

	private void addNotification(String uuid, Notification notification) {
		this.notification.addNotification(uuid, notification);
	}

	public String[] getOldConsoleLog() {
		return log.toArray(new String[0]);
	}

	public String[] getOldChatLog() {
		return chat.toArray(new String[0]);
	}

	public Notification[] getOldNotification() {
		return notification.getAll();
	}

	public String[] getOnlinePlayerNames() {
		Player[] players = getServer().getOnlinePlayers();
		String[] names = new String[players.length];
		for (int i = 0; i < players.length; i++) {
			names[i] = players[i].getName();
		}
		return names;
	}

	public File getRoot() throws IOException {
		return getDataFolder().getCanonicalFile().getParentFile().getParentFile();
	}

	public void addAdditionalInfoCreator(AdditionalInfoCreator creator) {
		this.additional_info_creators.add(creator);
	}

	public RemoteControllerAPI getAPI() {
		return api;
	}

	public ArrayList<String> getAdditionalInfo() {
		ArrayList<String> datas = new ArrayList<>();
		for (AdditionalInfoCreator creator : additional_info_creators) {
			AdditionalInfo info = creator.createAdditionalInfo();
			datas.add(new String(Base64Coder.encode(info.toString().getBytes(Charset.forName("UTF-8")))));
		}
		return datas;
	}

	public String getVersion() {
		return getDescription().getVersion();
	}

	public boolean isChatTypeBroadcast() {
		return chat_event_type_is_broadcast;
	}

	public String getMinecraftVersion() {
		return getServer().getBukkitVersion().split("-")[0];
	}

	public void setNotificationState(String uuid, boolean set_value) {
		notification.setNotificationState(uuid, set_value);
	}

	public void markAsConsumedAllNotification() {
		notification.markAsConsumedAll();
	}
}
