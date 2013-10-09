package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.PlayersData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.LogUtil;

public class CommandPlayers implements Command {

	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		LogUtil.d("CommandPlayers", arg);
		String[] datas;
		if (arg.length() == 0) {
			datas = new String[0];
		} else {
			datas = arg.split(":");
		}
		PlayersData data = new PlayersData();
		data.setPlayers(datas);
		return data;
	}

}
