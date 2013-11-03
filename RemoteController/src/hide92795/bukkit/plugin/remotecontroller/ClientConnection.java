package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.command.Command;
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
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.java_websocket.WebSocket;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import com.google.common.primitives.Bytes;

public class ClientConnection {
	private final RemoteController plugin;
	private final InetSocketAddress address;
	private WebSocket socket;
	private AtomicBoolean send_old_log = new AtomicBoolean(false);
	private AtomicBoolean send_old_chat = new AtomicBoolean(false);
	private String user;
	private AtomicBoolean auth = new AtomicBoolean(false);
	private AtomicBoolean sendConsoleLog = new AtomicBoolean(false);
	private AtomicBoolean sendChatLog = new AtomicBoolean(false);
	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	private AtomicReference<byte[]> key;


	public ClientConnection(RemoteController plugin, InetSocketAddress address, WebSocket socket) {
		this.plugin = plugin;
		this.socket = socket;
		this.address = address;
	}

	public void close() {
		if (user == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("The connection has been attempted from \"");
			sb.append(address.getAddress().getCanonicalHostName());
			sb.append("\"(");
			sb.append(address.getAddress().getHostAddress());
			sb.append(")");
			plugin.getLogger().info(sb.toString());
		} else {
			plugin.getLogger().info("User \"" + user + "\" has logged off.");
		}
		plugin.removeConnection(address);
		if (socket != null) {
			if (!socket.isClosed()) {
				try {
					socket.close();
				} catch (Exception e) {
					plugin.getLogger().severe("An error has occured closing connection!");
					e.printStackTrace();
				}
			}
			socket = null;
		}
	}

	public void start() {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(1024);
			KeyPair keyPair = keygen.generateKeyPair();

			privateKey = (RSAPrivateKey) keyPair.getPrivate();
			publicKey = (RSAPublicKey) keyPair.getPublic();
		} catch (Exception e) {
			plugin.getLogger().severe("An error has occured in creating RSA key!");
			e.printStackTrace();
			close();
		}
		// Send public key
		String modules = publicKey.getModulus().toString();
		String publicExponent = publicKey.getPublicExponent().toString();
		send(modules + ":" + publicExponent);
	}

	public void setConsoleLogSendState(boolean send) {
		sendConsoleLog.set(send);
		if (!send_old_log.get() && send) {
			sendOldLog();
		}
	}

	private void sendOldLog() {
		String[] old_log = plugin.getOldConsoleLog();
		for (String str : old_log) {
			send("CONSOLE", 0, str);
		}
	}

	public boolean isSendConsoleLog() {
		return sendConsoleLog.get();
	}

	public void setChatLogSendState(boolean send) {
		sendChatLog.set(send);
		if (!send_old_chat.get() && send) {
			sendOldChat();
		}
	}

	private void sendOldChat() {
		String[] old_chat = plugin.getOldChatLog();
		for (String str : old_chat) {
			send("CHAT", 0, str);
		}
	}

	public boolean isSendChatLog() {
		return sendChatLog.get();
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
			byte[] result_b = cipher.doFinal(text.getBytes(Charset.forName("UTF-8")));

			result = new String(Base64Coder.encode(Bytes.concat(iv, result_b)));
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
			byte[] data = Base64Coder.decode(text);
			byte[] iv = Arrays.copyOfRange(data, 0, 16);
			byte[] text_b = Arrays.copyOfRange(data, 16, data.length);

			Key keySpec = new SecretKeySpec(key.get(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);

			byte[] resultBytes = cipher.doFinal(text_b);
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
				if (data == null) {
					// Socket Close
					close();
					return;
				}
				byte[] receive_key_decoded = Base64Coder.decode(data);
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				ClientConnection.this.key = new AtomicReference<byte[]>(cipher.doFinal(receive_key_decoded));
				send("REQUEST_AUTH", 0, "");
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		} else {
			try {
				if (data == null) {
					// Socket Close
					close();
					return;
				}
				final String[] command_s = decrypt(data).split(":", 3);
				final Command command = Commands.commands.get(command_s[0]);
				final int pid = Integer.parseInt(command_s[1]);

				if (command == null) {
					send("ERROR", pid, "UK_CMD");
				} else {
					if (command.mustRunOnMainThread()) {
						new BukkitRunnable() {
							@Override
							public void run() {
								command.doCommand(plugin, ClientConnection.this, pid, command_s[2]);
							}
						}.runTaskLater(plugin, 0);
					} else {
						command.doCommand(plugin, ClientConnection.this, pid, command_s[2]);
					}
				}
			} catch (Exception e) {
				close();
			}
		}

	}
}
