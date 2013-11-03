package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.command.Command;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
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
import java.util.concurrent.ConcurrentLinkedQueue;
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
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import com.google.common.primitives.Bytes;

public class ClientConnection {
	private final RemoteController plugin;
	private final Socket socket;
	private final InetAddress address;
	private AtomicBoolean send_old_log = new AtomicBoolean(false);
	private AtomicBoolean send_old_chat = new AtomicBoolean(false);
	private String user;
	private AtomicBoolean running = new AtomicBoolean(true);
	private AtomicBoolean auth = new AtomicBoolean(false);
	private AtomicBoolean sendConsoleLog = new AtomicBoolean(false);
	private AtomicBoolean sendChatLog = new AtomicBoolean(false);
	private SendConnection send;
	private ReceiveConnection receive;
	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	private AtomicReference<byte[]> key;


	public ClientConnection(RemoteController plugin, InetAddress address, Socket socket) {
		this.plugin = plugin;
		this.socket = socket;
		this.address = address;
	}

	public void close() {
		if (user == null) {
			plugin.getLogger().info("The connection has been attempted from " + address.getCanonicalHostName());
		} else {
			plugin.getLogger().info("User \"" + user + "\" has logged off.");
		}
		running.set(false);
		plugin.removeConnection(address);
		if (socket != null) {
			try {
				socket.shutdownInput();
			} catch (IOException e) {
			}
			try {
				socket.shutdownOutput();
			} catch (IOException e) {
			}
			try {
				socket.close();
			} catch (IOException e) {
				plugin.getLogger().severe("An error has occured closing connection!");
				e.printStackTrace();
			}
		}
	}

	public void start() throws IOException {
		try {
			send = new SendConnection(new PrintWriter(socket.getOutputStream(), true));
			receive = new ReceiveConnection(new BufferedReader(new InputStreamReader(this.socket.getInputStream())));
			send.start();
			receive.start();
		} catch (IOException e) {
			plugin.getLogger().severe("An error has occured in connection!");
			e.printStackTrace();
			close();
		}
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
		send.send(modules + ":" + publicExponent);
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

	public boolean isRunning() {
		return this.running.get();
	}

	public String getUser() {
		return user;
	}

	public void send(String cmd, int pid, String text) {
		String[] pieces = { cmd, String.valueOf(pid), text };
		String sendText = StringUtils.join(pieces, ":");
		send.send(encrypt(sendText));
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
	private class SendConnection extends Thread {
		private final PrintWriter out;
		private final ConcurrentLinkedQueue<String> queue;

		public SendConnection(PrintWriter out) {
			this.out = out;
			this.queue = new ConcurrentLinkedQueue<>();
		}

		@Override
		public void run() {
			while (running.get()) {
				try {
					String text = queue.poll();
					if (text != null) {
						out.println(text);
						out.flush();
					}
					Thread.sleep(10);
				} catch (Exception e) {
					running.set(false);
					close();
				}
			}
		}

		private void send(String text) {
			queue.add(text);
		}
	}

	private class ReceiveConnection extends Thread {
		private final BufferedReader in;

		public ReceiveConnection(BufferedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			try {
				String receive_key = in.readLine();
				if (receive_key == null) {
					// Socket Close
					close();
					return;
				}
				byte[] receive_key_decoded = Base64Coder.decode(receive_key);
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				ClientConnection.this.key = new AtomicReference<byte[]>(cipher.doFinal(receive_key_decoded));
				send("REQUEST_AUTH", 0, "");
			} catch (Exception e) {
				e.printStackTrace();
				running.set(false);
				close();
			}
			while (running.get()) {
				try {
					String inputLine = in.readLine();

					if (inputLine == null) {
						// Socket Close
						close();
						break;
					}

					final String[] command_s = decrypt(inputLine).split(":", 3);
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
					running.set(false);
					close();
				}
			}
		}
	}
}
