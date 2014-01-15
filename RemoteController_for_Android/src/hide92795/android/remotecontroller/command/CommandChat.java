package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.ChatData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class CommandChat implements Command {

	@Override
	public ReceiveData doCommand(final Connection connection, int pid, final String arg) {
		connection.session.getHandler().post(new Runnable() {
			@Override
			public void run() {
				ChatData data = new ChatData();
				data.setMessage(arg);
				connection.session.getChatAdapter().add(data);
			}
		});
		// pid is 0
		return null;
	}
}
