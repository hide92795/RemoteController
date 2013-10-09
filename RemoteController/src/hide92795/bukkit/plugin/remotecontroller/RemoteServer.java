package hide92795.bukkit.plugin.remotecontroller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoteServer extends Thread {
	private final RemoteController plugin;
	private ServerSocket socket = null;
	private ConcurrentHashMap<InetAddress, ClientConnection> clients;
	public AtomicBoolean running = new AtomicBoolean(true);


	public RemoteServer(RemoteController plugin) {
		this.plugin = plugin;
		this.clients = new ConcurrentHashMap<>();
	}

	@Override
	public void run() {
		try {
			socket = new ServerSocket(plugin.config.port);
			socket.setSoTimeout(0);
		} catch (IOException e1) {
			plugin.getLogger().severe("An error has occured starting server!");
			running.set(false);
			e1.printStackTrace();
		}
		while (running.get()) {
			try {
				Socket client_socket = socket.accept();
				socket.setSoTimeout(0);
				InetAddress clientAddress = client_socket.getInetAddress();
				if (clients.containsKey(clientAddress)) {
					clients.get(clientAddress).close();
				}
				ClientConnection client = new ClientConnection(plugin, clientAddress, client_socket);
				client.start();
				clients.put(clientAddress, client);
			} catch (SocketException e) {
			} catch (IOException e) {
				plugin.getLogger().severe("An error has occured in server!");
				e.printStackTrace();
			}
		}
	}

	public void stopServer() {
		running.set(false);
		for (InetAddress key : clients.keySet()) {
			clients.get(key).close();
			clients.remove(key);
		}

		try {
			socket.close();
		} catch (IOException e) {
			plugin.getLogger().severe("An error has occured closing server!");
			e.printStackTrace();
		}
	}

	public void sendConsoleLog(String message) {
		for (InetAddress address : clients.keySet()) {
			ClientConnection connection = clients.get(address);
			if (connection.isRunning() && connection.isSendConsoleLog()) {
				connection.send("CONSOLE", 0, message);
			}
		}
	}

	public void removeConnection(InetAddress address) {
		clients.remove(address);
	}

	public void sendChatLog(String message) {
		for (InetAddress address : clients.keySet()) {
			ClientConnection connection = clients.get(address);
			if (connection.isRunning() && connection.isSendChatLog()) {
				connection.send("CHAT", 0, message);
			}
		}
	}
}
