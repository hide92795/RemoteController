package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.command.Command;
import hide92795.android.remotecontroller.data.ConnectionData;
import hide92795.android.remotecontroller.receivedata.FileData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.Base64Coder;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.os.AsyncTask;


public class Connection {
	public final Requests requests;
	private final ConcurrentHashMap<Integer, ReceiveListener> listeners;
	private final ConcurrentHashMap<Integer, String> sendedrequests;
	private Session session;
	private AtomicBoolean running = new AtomicBoolean(true);
	private AtomicBoolean auth = new AtomicBoolean(false);
	private byte[] key;
	private SendConnection send;
	private ReceiveConnection receive;
	private int pid = 1;
	private Socket socket;
	private final ConnectionData connection_data;

	public Connection(Session session, ConnectionData connection_data) {
		this.requests = new Requests();
		this.session = session;
		this.session.setConnection(this);
		this.connection_data = connection_data;
		this.listeners = new ConcurrentHashMap<Integer, ReceiveListener>();
		this.sendedrequests = new ConcurrentHashMap<Integer, String>();
	}

	public void start() {
		AsyncTask<Void, Void, Socket> connect = new AsyncTask<Void, Void, Socket>() {
			@Override
			protected Socket doInBackground(Void... params) {
				try {
					socket = new Socket(connection_data.getAddress(), connection_data.getPort());
					socket.setSoTimeout(0);
					send = new SendConnection(new PrintWriter(socket.getOutputStream(), true));
					receive = new ReceiveConnection(new BufferedReader(new InputStreamReader(socket.getInputStream())));
				} catch (Exception e) {
					session.close(true, e.getMessage());
				}
				return socket;
			}

			@Override
			protected void onPostExecute(Socket result) {
				if (running.get()) {
					LogUtil.d("", "Connection start.");
					Connection.this.socket = socket;
					send.start();
					receive.start();
				}
			}
		};
		connect.execute();
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
		if (running.get()) {
			running.set(false);
			if (socket != null) {
				LogUtil.d("", "Close socket.");
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
				}
				socket = null;
			}
		}
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public void authorize() {
		this.auth.set(true);
	}

	private void send(String cmd, int pid, String text) {
		sendedrequests.put(pid, cmd);
		send.send(StringUtils.join(":", cmd, pid, text));
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
			byte[] result_b = cipher.doFinal(text.getBytes(Charset.forName("UTF-8")));

			byte[] data = new byte[iv.length + result_b.length];
			System.arraycopy(iv, 0, data, 0, iv.length);
			System.arraycopy(result_b, 0, data, iv.length, result_b.length);

			result = new String(Base64Coder.encode(data));
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

			Key keySpec = new SecretKeySpec(key, "AES");
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
			this.queue = new ConcurrentLinkedQueue<String>();
		}

		private void send(String text) {
			queue.add(text);
		}

		@Override
		public void run() {
			while (running.get()) {
				try {
					String text = queue.poll();
					if (text != null) {
						out.println(encrypt(text));
						out.flush();
					}
					Thread.sleep(10);
				} catch (Exception e) {
					session.close(true, e.getMessage());
				}
			}
		}
	}

	private class ReceiveConnection extends Thread {
		private final BufferedReader in;

		public ReceiveConnection(BufferedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			// Key
			try {
				String key_s = in.readLine();
				if (key_s == null) {
					session.close(true, "Failed to receive encryption key.");
					return;
				}
				setKey(key_s.getBytes(Charset.forName("UTF-8")));
				LogUtil.d("", "Receive Key.");
				requests.sendAuthorizeData();
			} catch (IOException e) {
				e.printStackTrace();
				session.close(true, e.getMessage());
			}

			while (running.get()) {
				try {
					String inputLine = in.readLine();

					if (inputLine == null) {
						session.close(true, "Disconnected by server.");
						break;
					}
					String[] command_s = decrypt(inputLine).split(":", 3);

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
			send("AUTH", 0, StringUtils.join(":", connection_data.getUsername(), connection_data.getPassword(), "test"));
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