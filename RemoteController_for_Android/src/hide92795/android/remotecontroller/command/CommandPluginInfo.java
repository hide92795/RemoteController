package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.CommandData;
import hide92795.android.remotecontroller.receivedata.PermissionData;
import hide92795.android.remotecontroller.receivedata.PluginInfoData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import java.util.LinkedHashMap;
import java.util.Map;
import net.arnx.jsonic.JSON;

public class CommandPluginInfo implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		LinkedHashMap<String, Object> datas = JSON.decode(arg);

		Map<String, Map<String, String>> commands = (Map<String, Map<String, String>>) datas.get("COMMANDS");
		CommandData[] command_datas = new CommandData[commands.size()];
		int i = 0;
		for (String command : commands.keySet()) {
			Map<String, String> command_desc = commands.get(command);
			CommandData data = new CommandData();
			data.setCommand(command);
			data.setAlias(command_desc.get("ALIASES"));
			data.setPermisson(command_desc.get("PERMISSION"));
			data.setDescription(command_desc.get("DESCRIPTION"));
			data.setUsage(command_desc.get("USAGE"));
			command_datas[i] = data;
			i++;
		}

		Map<String, Map<String, String>> permissions = (Map<String, Map<String, String>>) datas.get("PERMISSIONS");
		PermissionData[] permission_datas = new PermissionData[permissions.size()];
		i = 0;
		for (String permission : permissions.keySet()) {
			Map<String, String> permission_desc = permissions.get(permission);
			PermissionData data = new PermissionData();
			data.setPermission(permission);
			data.setDescription((String) permission_desc.get("DESCRIPTION"));
			permission_datas[i] = data;
			i++;
		}

		PluginInfoData data = new PluginInfoData();
		data.setName((String) datas.get("NAME"));
		data.setVersion((String) datas.get("VERSION"));
		data.setAuthor((String) datas.get("AUTHOR"));
		data.setWeb((String) datas.get("WEB"));
		data.setDescription((String) datas.get("DESCRIPTION"));
		data.setCommands(command_datas);
		data.setPermissions(permission_datas);
		data.setEnabled((Boolean) datas.get("STATUS"));
		return data;
	}
}
