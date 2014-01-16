package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.ConnectionBase;
import hide92795.android.remotecontroller.ConnectionDataPair;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommand;
import hide92795.android.remotecontroller.util.CryptUtil;
import hide92795.android.remotecontroller.util.CryptUtil.RSAKeyExchangePair;
import hide92795.android.remotecontroller.util.LogUtil;
import hide92795.android.remotecontroller.util.StringUtils;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

public class AutoUpdateConnection extends ConnectionBase {
	private final AutoUpdateDataReceiver receiver;
	private AutoUpdateClientSocket socket;
	private AtomicBoolean key_excanged = new AtomicBoolean(false);
	private AtomicBoolean published = new AtomicBoolean(false);

	public AutoUpdateConnection(Session session, ConnectionDataPair pair) {
		super(pair.data);
		this.receiver = new AutoUpdateDataReceiver(session, this, pair.uuid);
	}

	@Override
	public void start() {
		try {
			this.socket = new AutoUpdateClientSocket(connection_data.getURI(), new Draft_17());
			socket.connect();
			LogUtil.d("[AutoUpdate] Connection start.");
		} catch (Exception e) {
			LogUtil.d("[AutoUpdate] Can't start the connecton.");
			LogUtil.exception(e);
		}
	}

	@Override
	protected void send(String cmd, int pid, String text) {
		try {
			sendData(CryptUtil.encrypt(StringUtils.join(":", cmd, pid, text), key));
			LogUtil.d("[AutoUpdate] Send request : " + cmd + ", pid : " + pid, ", data : " + text);
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
			close();
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
				LogUtil.d("[AutoUpdate] Received Public Key.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (data == null) {
					close();
					return;
				}
				String[] command_s = CryptUtil.decrypt(data, key).split(":", 3);

				if (command_s != null) {
					AutoUpdateCommand command = AutoUpdateCommands.commands.get(command_s[0]);
					int pid = Integer.parseInt(command_s[1]);
					LogUtil.d("[AutoUpdate] Receive command : " + command_s[0] + ", pid : " + pid, ", data : " + command_s[2]);
					if (command == null) {
						// Error
					} else {
						doCommand(command, pid, command_s[2]);
					}
				}
			} catch (Exception e) {
				close();
			}
		}
	}

	private void doCommand(AutoUpdateCommand command, final int pid, String raw_data) {
		receiver.onReceive(command, pid, raw_data);
	}

	@Override
	public void close() {
		if (socket != null) {
			LogUtil.d("[AutoUpdate] Close socket.");
			try {
				socket.close();
			} catch (Exception e) {
			}
			socket = null;
		}
		if (!published.get()) {
			published.set(true);
			receiver.publish();
		}
	}

	private class AutoUpdateClientSocket extends WebSocketClient {
		public AutoUpdateClientSocket(URI serverUri, Draft draft) {
			super(serverUri, draft);
		}

		@Override
		public void onClose(int arg0, String arg1, boolean remote) {
			LogUtil.d("[AutoUpdate] Connection close.");
			AutoUpdateConnection.this.close();
		}

		@Override
		public void onError(Exception arg0) {
			LogUtil.d("[AutoUpdate] An error has occurred at AutoUpdateClientSocket#onError().");
			AutoUpdateConnection.this.close();
		}

		@Override
		public void onMessage(String data) {
			receive(data);
		}

		@Override
		public void onOpen(ServerHandshake arg0) {
			LogUtil.d("[AutoUpdate] Connection open.");
		}
	}
}
