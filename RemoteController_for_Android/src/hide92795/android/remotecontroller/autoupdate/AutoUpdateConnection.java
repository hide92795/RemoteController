package hide92795.android.remotecontroller.autoupdate;

import hide92795.android.remotecontroller.ConnectionBase;
import hide92795.android.remotecontroller.ConnectionData;
import hide92795.android.remotecontroller.autoupdate.command.AutoUpdateCommand;
import hide92795.android.remotecontroller.util.CryptUtil;
import hide92795.android.remotecontroller.util.CryptUtil.RSAKeyExchangePair;
import hide92795.android.remotecontroller.util.LogUtil;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

public class AutoUpdateConnection extends ConnectionBase {
	private AutoUpdateClientSocket socket;
	private AtomicBoolean key_excanged = new AtomicBoolean(false);

	public AutoUpdateConnection(ConnectionData connection_data) {
		super(connection_data);
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

	}

	@Override
	protected void sendData(String data) {

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
					// session.close(true, "Disconnected by server.");
					return;
				}
				String[] command_s = CryptUtil.decrypt(data, key).split(":", 3);

				if (command_s != null) {
					AutoUpdateCommand command = AutoUpdateCommands.commands.get(command_s[0]);
					int pid = Integer.parseInt(command_s[1]);
					LogUtil.d("[AutoUpdate] Receive command : " + command_s[0] + ", pid : " + pid);
					if (command == null) {
					} else {
						doCommand(command, pid, command_s[2]);
					}
				}
			} catch (Exception e) {
				// session.close(true, e.getMessage());
			}
		}
	}

	private void doCommand(AutoUpdateCommand command, final int pid, String raw_data) {
		// if (pid == 0) {
		// command.doCommand(Connection.this, pid, raw_data);
		// } else {
		// final ReceiveData data = command.doCommand(Connection.this, pid, raw_data);
		// final ReceiveListener listener = listeners.get(pid);
		// if (listener != null) {
		// final String sended_cmd = sendedrequests.get(pid);
		// listeners.remove(pid);
		// sendedrequests.remove(pid);
		// session.getHandler().post(new Runnable() {
		// @Override
		// public void run() {
		// listener.onReceiveData(sended_cmd, pid, data);
		// }
		// });
		// }
		// }
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

	private class AutoUpdateClientSocket extends WebSocketClient {
		public AutoUpdateClientSocket(URI serverUri, Draft draft) {
			super(serverUri, draft);
		}

		@Override
		public void onClose(int arg0, String arg1, boolean remote) {
			AutoUpdateConnection.this.close();
		}

		@Override
		public void onError(Exception arg0) {
			LogUtil.d("[AutoUpdate] An error has occurred at AutoUpdateClientSocket#onError().");
			LogUtil.exception(arg0);
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
