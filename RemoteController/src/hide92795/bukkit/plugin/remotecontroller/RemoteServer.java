package hide92795.bukkit.plugin.remotecontroller;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class RemoteServer extends WebSocketServer {
	private int id = 0;
	private final RemoteController plugin;
	private ConcurrentHashMap<Integer, ClientConnection> clients;


	public RemoteServer(RemoteController plugin) {
		super(new InetSocketAddress(plugin.config.port), 1, Collections.singletonList((Draft) new Draft_17()));
		this.plugin = plugin;
		this.clients = new ConcurrentHashMap<>();
	}

	@Override
	public void onOpen(WebSocket client_socket, ClientHandshake handshake) {
		int client_id = id++;
		client_socket.setID(client_id);
		InetSocketAddress clientAddress = client_socket.getRemoteSocketAddress();
		plugin.getLogger().info("Connection start. (" + clientAddress.getAddress().getHostAddress() + ", ID: " + client_id + ")");
		if (clients.containsKey(client_id)) {
			clients.get(client_id).closed();
		}
		ClientConnection client = new ClientConnection(plugin, clientAddress, client_socket);
		try {
			client.start();
			clients.put(client_id, client);
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occurred in creating RSA key.");
		}
	}

	@Override
	public void onClose(WebSocket client_socket, int code, String reason, boolean remote) {
		int client_id = client_socket.getID();
		ClientConnection client = clients.remove(client_id);
		if (client != null) {
			client.closed();
		} else {
			plugin.getLogger().warning("ClientConnection didn't found in #onClose.");
		}
	}

	@Override
	public void onError(WebSocket client_socket, Exception ex) {
		if (client_socket != null) {
			plugin.getLogger().warning("An error has occured in the connection. : " + client_socket);
		} else {
			plugin.getLogger().severe("An internal error has occurred.");
			// if (ex instanceof BindException) {
			// plugin.getLogger().severe("If you use /reload command, the remote server couldn\'t restart successfully.");
			// plugin.getLogger().severe("Please RESTART your server.");
			// plugin.getServer().getPluginManager().disablePlugin(plugin);
			// return;
			// }
		}
		ex.printStackTrace();
		// plugin.getLogger().warning(ex.toString());
	}

	@Override
	public void onMessage(WebSocket client_socket, String message) {
		int id = client_socket.getID();
		if (clients.containsKey(id)) {
			clients.get(id).receive(message);
		} else {
			plugin.getLogger().severe("Client not found. ID:" + id);
		}
	}

	public void stopServer() {
		try {
			stop();
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured closing server!");
			e.printStackTrace();
		}
	}

	public void sendConsoleLog(String message) {
		for (Integer id : clients.keySet()) {
			ClientConnection connection = clients.get(id);
			if (connection.isSendConsoleLog()) {
				connection.send("CONSOLE", 0, message);
			}
		}
	}

	public void removeConnection(InetSocketAddress address) {
		clients.remove(address);
	}

	public void sendChatLog(String message) {
		for (Integer id : clients.keySet()) {
			ClientConnection connection = clients.get(id);
			if (connection.isSendChatLog()) {
				connection.send("CHAT", 0, message);
			}
		}
	}
}
