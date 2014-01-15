package hide92795.android.remotecontroller.autoupdate.command;

import hide92795.android.remotecontroller.autoupdate.AutoUpdateConnection;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.receivedata.ServerData;
import hide92795.android.remotecontroller.util.Base64Coder;
import hide92795.android.remotecontroller.util.StringUtils;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class AutoUpdateCommandServerInfo implements AutoUpdateCommand {
	@Override
	public ReceiveData doCommand(AutoUpdateConnection connection, int pid, String arg) {
		String[] datas = arg.split(":");
		String servername = new String(Base64Coder.decode(datas[0]), Charset.forName("UTF-8"));
		int port = Integer.parseInt(datas[1]);
		int max = Integer.parseInt(datas[2]);
		int current = Integer.parseInt(datas[3]);

		ArrayList<String> add_info_al = new ArrayList<String>();
		if (datas.length != 4) {
			for (int i = 4; i < datas.length; i++) {
				String add_info_s = new String(Base64Coder.decode(datas[i]), Charset.forName("UTF-8"));
				String[] add_info = add_info_s.split(":", 2);
				add_info_al.add("--" + add_info[0] + "--");
				add_info_al.add(add_info[1]);
				add_info_al.add("\n");
			}
			add_info_al.remove(add_info_al.size() - 1);
		}

		ServerData data = new ServerData();
		data.setServername(servername);
		data.setAddress(connection.getServerAddress() + ":" + port);
		data.setMax(max);
		data.setCurrent(current);
		data.setAddInfo(StringUtils.join("\n", add_info_al));
		return data;
	}
}
