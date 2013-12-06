package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.PluginListData;
import hide92795.android.remotecontroller.receivedata.PluginListData.PluginData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.Base64Coder;
import java.nio.charset.Charset;

public class CommandPluginList implements Command {
	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		String[] datas = arg.split(":");
		int size = datas.length;
		PluginData[] plugindatas = new PluginData[size];
		for (int i = 0; i < size; i++) {
			String data = datas[i];
			String data_dec = new String(Base64Coder.decode(data), Charset.forName("UTF-8"));
			int pos = data_dec.lastIndexOf(":");
			String name = data_dec.substring(0, pos);
			boolean enable = Boolean.parseBoolean(data_dec.substring(pos + 1));
			plugindatas[i] = new PluginData(name, enable);
		}
		PluginListData data = new PluginListData();
		data.setPlugins(plugindatas);
		return data;
	}
}
