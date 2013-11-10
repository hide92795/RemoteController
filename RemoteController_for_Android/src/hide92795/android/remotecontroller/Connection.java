package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.command.Command;
import hide92795.android.remotecontroller.receivedata.FileData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.Base64Coder;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.StringUtils;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.util.Base64;


public class Connection {
	public final Requests requests;
	private final ConcurrentHashMap<Integer, ReceiveListener> listeners;
	private final ConcurrentHashMap<Integer, String> sendedrequests;
	private final Session session;
	private AtomicBoolean key_excanged = new AtomicBoolean(false);
	private AtomicBoolean auth = new AtomicBoolean(false);
	private byte[] key;
	private int pid = 1;
	private ClientSocket socket;
	private final ConnectionData connection_data;

	public Connection(Session session, ConnectionData connection_data) {
		this.requests = new Requests();
		this.session = session;
		this.session.setConnection(this);
		this.connection_data = connection_data;
		this.listeners = new ConcurrentHashMap<Integer, ReceiveListener>();
		this.sendedrequests = new ConcurrentHashMap<Integer, String>();
		// this.key = StringUtils.getRandomString(16).getBytes(Charset.forName("UTF-8"));
	}

	public void start() {
		try {
			this.socket = new ClientSocket(connection_data.getURI(), new Draft_17());
			Thread thread = new Thread(socket);
			thread.start();
			LogUtil.d("", "Connection start.");
		} catch (Exception e) {
			session.close(true, e.getMessage());
		}
	}

	public String getServerAddress() {
		return connection_data.getAddress();
	}

	public int getServerPort() {
		return connection_data.getPort();
	}

	public synchronized void addListener(int pid, ReceiveListener listener) {
		listeners.put(pid, listener);
	}

	public Session getSession() {
		return session;
	}

	public void close() {
		if (socket != null) {
			LogUtil.d("", "Close socket.");
			try {
				socket.close();
			} catch (Exception e) {
			}
			socket = null;
		}
	}

	public void authorize() {
		this.auth.set(true);
	}

	private void send(String cmd, int pid, String text) {
		sendedrequests.put(pid, cmd);
		send(encrypt(StringUtils.join(":", cmd, pid, text)));
		LogUtil.d("", "Send request : " + cmd + ", pid : " + pid);
	}

	public int nextPid() {
		pid++;
		return pid;
	}

	private String encrypt(String text) {
		String result = "";
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
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
			byte[] text_b = Base64Coder.decode(encrypted_b64);

			Key keySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);

			byte[] decrypted = cipher.doFinal(text_b);
			result = new String(decrypted, Charset.forName("UTF-8"));
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

	private void send(String data) {
		try {
			socket.send(data);
		} catch (Exception e) {
			session.close(true, e.getMessage());
		}
	}

	private void receive(String data) {
		if (!key_excanged.get()) {
			// Key Excange
			key_excanged.set(true);
			try {
				String[] rsa_key_sa = data.split(":");
				String modules_s = rsa_key_sa[0];
				String publicExponent_s = rsa_key_sa[1];

				BigInteger modules = new BigInteger(modules_s);
				BigInteger publicExponent = new BigInteger(publicExponent_s);

				RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modules, publicExponent);

				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);

				// Gen key
				char[] password = UUID.randomUUID().toString().toCharArray();
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				byte[] salt = new byte[16];
				random.nextBytes(salt);
				SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				KeySpec spec = new PBEKeySpec(password, salt, 10, 128);
				SecretKey tmp = factory.generateSecret(spec);
				this.key = tmp.getEncoded();

				byte[] common_key_base64 = Base64.encodeBytesToBytes(key);
				byte[] common_key_encrypted = cipher.doFinal(common_key_base64);
				char[] common_key_base64_encoded = Base64Coder.encode(common_key_encrypted);
				send(String.valueOf(common_key_base64_encoded));
				LogUtil.d("", "Received Public Key.");
			} catch (Exception e) {
				e.printStackTrace();
				session.close(true, e.getMessage());
			}
		} else {
			try {
				if (data == null) {
					session.close(true, "Disconnected by server.");
					return;
				}
				String[] command_s = decrypt(data).split(":", 3);

				if (command_s != null) {
					Command command = Commands.commands.get(command_s[0]);
					int pid = Integer.parseInt(command_s[1]);
					LogUtil.d("", "Receive command : " + command_s[0] + ", pid : " + pid);
					if (command == null) {
					} else {
						doCommand(command, pid, command_s[2]);
					}
				}
				Thread.sleep(10);
			} catch (Exception e) {
				session.close(true, e.getMessage());
			}
		}
	}

	private class ClientSocket extends WebSocketClient {
		public ClientSocket(URI serverUri, Draft draft) {
			super(serverUri, draft);
		}

		@Override
		public void onClose(int arg0, String arg1, boolean remote) {
			session.close(true, "Disconnected by server.");
		}

		@Override
		public void onError(Exception arg0) {
			session.close(true, "An error has occurred.\n" + arg0.getClass().toString() + "\n" + arg0.getLocalizedMessage());
		}

		@Override
		public void onMessage(String data) {
			receive(data);
		}

		@Override
		public void onOpen(ServerHandshake arg0) {
			LogUtil.d("", "Connection opened.");
		}

	}

	private void doCommand(Command command, final int pid, String raw_data) {
		if (pid == 0) {
			command.doCommand(Connection.this, pid, raw_data);
		} else {
			final ReceiveData data = command.doCommand(Connection.this, pid, raw_data);
			final ReceiveListener listener = listeners.get(pid);
			if (listener != null) {
				final String sended_cmd = sendedrequests.get(pid);
				listeners.remove(pid);
				sendedrequests.remove(pid);
				session.getHandler().post(new Runnable() {
					@Override
					public void run() {
						listener.onReceiveData(sended_cmd, pid, data);
					}
				});
			}
		}
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
	}
}