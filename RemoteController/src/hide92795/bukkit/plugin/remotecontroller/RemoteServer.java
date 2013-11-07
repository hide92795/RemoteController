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
		plugin.getLogger().info("Connection start. (" + clientAddress.getAddress().getHostAddress() + ")");
		if (clients.containsKey(clientAddress)) {
			clients.get(clientAddress).closed();
		}
		ClientConnection client = new ClientConnection(plugin, clientAddress, client_socket);
		try {
			client.start();
			clients.put(clientAddress, client);
		} catch (Exception e) {
			plugin.getLogger().severe("An erro has occurred in creating RSA key.");
		}
	}

	@Override
	public void onClose(WebSocket client_socket, int code, String reason, boolean remote) {
		InetSocketAddress clientAddress = client_socket.getRemoteSocketAddress();
		ClientConnection client = clients.remove(clientAddress);
		if (client != null) {
			client.closed();
		}
	}

	@Override
	public void onError(WebSocket client_socket, Exception ex) {
		if (client_socket != null) {
			plugin.getLogger().warning("An error has occured in the connection. : " + client_socket.getRemoteSocketAddress().getAddress().getHostAddress());
		} else {
			plugin.getLogger().warning("An internal error has occurred.");
		}
		plugin.getLogger().warning(ex.getLocalizedMessage());
	}

	@Override
	public void onMessage(WebSocket client_socket, String message) {
		InetSocketAddress clientAddress = client_socket.getRemoteSocketAddress();
		if (clients.containsKey(clientAddress)) {
			clients.get(clientAddress).receive(message);
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
