package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.FileData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;

public class CommandFileOpen implements Command {
	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		String[] datas = arg.split(":", 3);
		FileData data = new FileData();
		data.setFile(datas[0]);
		data.setEncoding(datas[1]);
		data.setData(datas[2]);
		return data;
	}
}
