package hide92795.android.remotecontroller.autoupdate.command;

import hide92795.android.remotecontroller.autoupdate.AutoUpdateConnection;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.receivedata.ServerIconData;
import hide92795.android.remotecontroller.util.Base64Coder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AutoUpdateCommandServerIcon implements AutoUpdateCommand {
	@Override
	public ReceiveData doCommand(AutoUpdateConnection connection, int pid, String arg) {
		ServerIconData data = new ServerIconData();
		if (arg.length() != 0) {
			byte[] bytes = Base64Coder.decode(arg.toCharArray());
			Bitmap icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			data.setIcon(icon);
		}
		return data;
	}
}
