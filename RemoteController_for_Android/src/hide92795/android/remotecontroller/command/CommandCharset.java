package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.ui.dialog.CharsetDialogFragment;

public class CommandCharset implements Command {
	@Override
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		String[] charsets = arg.split(":");
		CharsetDialogFragment.setCharsets(charsets);
		// pid is 0
		return null;
	}
}
