package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.command.Command;
import hide92795.bukkit.plugin.remotecontroller.compatibility.CommandDispatcher;
import hide92795.bukkit.plugin.remotecontroller.compatibility.CommandDispatcherWithBukkitRunnable;
import hide92795.bukkit.plugin.remotecontroller.notification.Notification;
import hide92795.bukkit.plugin.remotecontroller.org.apache.commons.lang3.StringUtils;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.util.Base64;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ClientConnection {
	private final RemoteController plugin;
	private final InetSocketAddress address;
	private final WebSocket socket;
	private AtomicBoolean sended_old_log = new AtomicBoolean(false);
	private AtomicBoolean sended_old_chat = new AtomicBoolean(false);
	private AtomicBoolean sended_old_notification = new AtomicBoolean(false);
	private String user;
	private AtomicBoolean auth = new AtomicBoolean(false);
	private AtomicBoolean sendConsoleLog = new AtomicBoolean(false);
	private AtomicBoolean sendChatLog = new AtomicBoolean(false);
	private AtomicBoolean sendNotificationLog = new AtomicBoolean(false);
	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	private AtomicReference<byte[]> key;

	private static final CommandDispatcher COMMAND_DISPATCHER;
	static {
		if (isAvailableBukkitRunnable()) {
			COMMAND_DISPATCHER = new CommandDispatcherWithBukkitRunnable();
		} else {
			COMMAND_DISPATCHER = new CommandDispatcher();
		}
	}

	private static boolean isAvailableBukkitRunnable() {
		try {
			Class.forName("org.bukkit.scheduler.BukkitRunnable");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public ClientConnection(RemoteController plugin, InetSocketAddress address, WebSocket socket) {
		this.plugin = plugin;
		this.socket = socket;
		this.address = address;
	}

	public void closed() {
		if (auth.get()) {
			plugin.getLogger().info("User \"" + user + "\" has logged off.");
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("The connection has been attempted from \"");
			sb.append(address.toString());
			sb.append("\"");
			plugin.getLogger().info(sb.toString());
		}
	}

	public void close() {
		try {
			if (!socket.isClosed()) {
				socket.close(CloseFrame.GOING_AWAY);
			}
		} catch (Error e) {
		}
	}

	public void start() throws Exception {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(1024);
		KeyPair keyPair = keygen.generateKeyPair();

		privateKey = (RSAPrivateKey) keyPair.getPrivate();
		publicKey = (RSAPublicKey) keyPair.getPublic();

		// Send public key
		String modules = publicKey.getModulus().toString();
		String publicExponent = publicKey.getPublicExponent().toString();
		send(modules + ":" + publicExponent);
	}

	public void setConsoleLogSendState(boolean send) {
		sendConsoleLog.set(send);
		if (!sended_old_log.get() && send) {
			sendOldLog();
		}
	}

	public void setChatLogSendState(boolean send) {
		sendChatLog.set(send);
		if (!sended_old_chat.get() && send) {
			sendOldChat();
		}
	}

	public void setNotificationLogSendState(boolean send) {
		sendNotificationLog.set(send);
		if (!sended_old_notification.get() && send) {
			sendOldNotification();
		}
	}

	public boolean isSendConsoleLog() {
		return sendConsoleLog.get();
	}

	public boolean isSendChatLog() {
		return sendChatLog.get();
	}

	public boolean isSendNotificationLog() {
		return sendNotificationLog.get();
	}

	private void sendOldLog() {
		String[] old_log = plugin.getOldConsoleLog();
		for (String str : old_log) {
			send("CONSOLE", 0, str);
		}
	}

	private void sendOldChat() {
		String[] old_chat = plugin.getOldChatLog();
		for (String str : old_chat) {
			send("CHAT", 0, str);
		}
	}

	private void sendOldNotification() {
		Notification[] old_notification = plugin.getOldNotification();
		for (Notification notification : old_notification) {
			send("NOTIFICATION", 0, notification.toString());
		}
	}

	public void sendCharset() {
		Set<String> set = Charset.availableCharsets().keySet();
		send("CHARSET", 0, StringUtils.join(set, ":"));
	}

	public void authorize(String user) {
		this.user = user;
		this.auth.set(true);
	}

	public boolean isAuthorized() {
		return this.auth.get();
	}

	public String getUser() {
		return user;
	}

	public void send(String cmd, int pid, String text) {
		String[] pieces = { cmd, String.valueOf(pid), text };
		String sendText = StringUtils.join(pieces, ":");
		send(encrypt(sendText));
	}

	private String encrypt(String text) {
		String result = "";
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.get(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			byte[] iv = cipher.getIV();
			byte[] encrypted = cipher.doFinal(text.getBytes(Charset.forName("UTF-8")));

			String iv_b64 = String.valueOf(Base64Coder.encode(iv));
			String encrypted_b64 = String.valueOf(Base64Coder.encode(encrypted));

			result = iv_b64 + ":" + encrypted_b64;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String decrypt(String text) {
		String result = "";
		try {
			String[] datas = text.split(":");
			String iv_b64 = datas[0];
			String encrypted_b64 = datas[1];

			byte[] iv = Base64Coder.decode(iv_b64);
			byte[] encrypted = Base64Coder.decode(encrypted_b64);

			Key keySpec = new SecretKeySpec(key.get(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);

			byte[] resultBytes = cipher.doFinal(encrypted);
			result = new String(resultBytes, Charset.forName("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return result;
	}

	private synchronized void send(String data) {
		if (socket != null && socket.isOpen()) {
			try {
				socket.send(data);
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
	}

	public synchronized void receive(String data) {
		if (this.key == null) {
			try {
				byte[] receive_key_decoded = Base64Coder.decode(data);
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				byte[] key_b64 = cipher.doFinal(receive_key_decoded);
				byte[] key = Base64.decode(key_b64);

				ClientConnection.this.key = new AtomicReference<byte[]>(key);
				send("REQUEST_AUTH", 0, "");
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		} else {
			try {
				final String[] command_s = decrypt(data).split(":", 3);
				final Command command = Commands.commands.get(command_s[0]);
				final int pid = Integer.parseInt(command_s[1]);

				if (command == null) {
					send("ERROR", pid, "UK_CMD");
				} else {
					COMMAND_DISPATCHER.dispatch(command, plugin, this, pid, command_s[2]);
				}
			} catch (Exception e) {
				closed();
			}
		}

	}
}
