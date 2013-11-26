package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.ConsoleData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class CommandConsole implements Command {

	@Override
	public ReceiveData doCommand(final Connection connection, int pid, final String arg) {
		connection.getSession().getHandler().post(new Runnable() {
			@Override
			public void run() {
				ConsoleData data = new ConsoleData();
				String[] datas = arg.split("-", 3);
				data.setDate(datas[0]);
				data.setLogLevel(datas[1]);
				data.setText(datas[2]);
				connection.getSession().getConsoleAdapter().add(data);
			}
		});
		// pid is 0
		return null;
	}
}
