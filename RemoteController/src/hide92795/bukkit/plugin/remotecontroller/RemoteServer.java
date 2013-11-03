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
	private final RemoteController plugin;
	private ConcurrentHashMap<InetSocketAddress, ClientConnection> clients;

	public RemoteServer(RemoteController plugin) {
		super(new InetSocketAddress(plugin.config.port), Collections.singletonList((Draft) new Draft_17()));
		this.plugin = plugin;
		this.clients = new ConcurrentHashMap<>();
	}

	@Override
	public void onOpen(WebSocket client_socket, ClientHandshake handshake) {
		InetSocketAddress clientAddress = client_socket.getRemoteSocketAddress();
		plugin.getLogger().info("Connection start. (" + clientAddress.getAddress().getCanonicalHostName() + ")");
		if (clients.containsKey(clientAddress)) {
			clients.get(clientAddress).close();
		}
		ClientConnection client = new ClientConnection(plugin, clientAddress, client_socket);
		client.start();
		clients.put(clientAddress, client);
	}

	@Override
	public void onClose(WebSocket client_socket, int code, String reason, boolean remote) {
		InetSocketAddress clientAddress = client_socket.getRemoteSocketAddress();
		if (clients.containsKey(clientAddress)) {
			clients.get(clientAddress).close();
		}
	}

	@Override
	public void onError(WebSocket client_socket, Exception ex) {
		InetSocketAddress clientAddress = client_socket.getRemoteSocketAddress();
		if (clients.containsKey(clientAddress)) {
			clients.get(clientAddress).close();
		}
		plugin.getLogger().warning("The connection is stopped by error.");
		ex.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket client_socket, String message) {
		InetSocketAddress clientAddress = client_socket.getRemoteSocketAddress();
		if (clients.containsKey(clientAddress)) {
			clients.get(clientAddress).receive(message);
		}
	}

	public void stopServer() {
		for (InetSocketAddress key : clients.keySet()) {
			clients.get(key).close();
			clients.remove(key);
		}

		try {
			stop();
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured closing server!");
			e.printStackTrace();
		}
	}

	public void sendConsoleLog(String message) {
		for (InetSocketAddress address : clients.keySet()) {
			ClientConnection connection = clients.get(address);
			if (connection.isSendConsoleLog()) {
				connection.send("CONSOLE", 0, message);
			}
		}
	}

	public void removeConnection(InetSocketAddress address) {
		clients.remove(address);
	}

	public void sendChatLog(String message) {
		for (InetSocketAddress address : clients.keySet()) {
			ClientConnection connection = clients.get(address);
			if (connection.isSendChatLog()) {
				connection.send("CHAT", 0, message);
			}
		}
	}
}
