package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.command.Command;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.CryptUtil;
import hide92795.android.remotecontroller.util.CryptUtil.RSAKeyExchangePair;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.StringUtils;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;


public class Connection extends ConnectionBase {
	public final Session session;
	private final ConcurrentHashMap<Integer, ReceiveListener> listeners;
	private final ConcurrentHashMap<Integer, String> sendedrequests;
	private AtomicBoolean key_excanged = new AtomicBoolean(false);
	private ClientSocket socket;

	public Connection(Session session, ConnectionData connection_data) {
		super(connection_data);
		this.session = session;
		this.session.setConnection(this);
		this.listeners = new ConcurrentHashMap<Integer, ReceiveListener>();
		this.sendedrequests = new ConcurrentHashMap<Integer, String>();
	}

	@Override
	public void start() {
		try {
			this.socket = new ClientSocket(connection_data.getURI(), new Draft_17());
			socket.connect();
			LogUtil.d("Connection start.");
		} catch (Exception e) {
			LogUtil.d("Can't start the connecton.");
			LogUtil.exception(e);
			session.close(true, e.getMessage());
		}
	}

	public synchronized void addListener(int pid, ReceiveListener listener) {
		listeners.put(pid, listener);
	}

	@Override
	public void close() {
		if (socket != null) {
			LogUtil.d("Close socket.");
			try {
				socket.close();
			} catch (Exception e) {
			}
			socket = null;
		}
	}

	@Override
	protected void send(String cmd, int pid, String text) {
		try {
			sendedrequests.put(pid, cmd);
			sendData(CryptUtil.encrypt(StringUtils.join(":", cmd, pid, text), key));
			LogUtil.d("Send request : " + cmd + ", pid : " + pid);
			// LogUtil.d("Send request : " + cmd + ", pid : " + pid + ", data : " + text);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void sendData(String data) {
		try {
			socket.send(data);
		} catch (Exception e) {
			session.close(true, e.getMessage());
		}
	}

	@Override
	protected void receive(String data) {
		if (!key_excanged.get()) {
			// Key Excange
			key_excanged.set(true);
			try {
				RSAKeyExchangePair pair = CryptUtil.rsaKeyExchange(data);
				this.key = pair.key;
				sendData(String.valueOf(pair.common_key_base64_encoded));
				LogUtil.d("Received Public Key.");
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
				String[] command_s = CryptUtil.decrypt(data, key).split(":", 3);

				if (command_s != null) {
					Command command = Commands.commands.get(command_s[0]);
					int pid = Integer.parseInt(command_s[1]);
					LogUtil.d("Receive command : " + command_s[0] + ", pid : " + pid);
					if (command == null) {
					} else {
						doCommand(command, pid, command_s[2]);
					}
				}
			} catch (Exception e) {
				session.close(true, e.getMessage());
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
			LogUtil.d("An error has occurred at ClientSocket#onError().");
			LogUtil.exception(arg0);
			session.close(true, "An error has occurred.\n" + arg0.toString());
		}

		@Override
		public void onMessage(String data) {
			receive(data);
		}

		@Override
		public void onOpen(ServerHandshake arg0) {
			LogUtil.d("Connection open.");
		}
	}
}