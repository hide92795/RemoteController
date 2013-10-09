package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.activity.MainActivity;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;

public class CommandAuthentication implements Command {
	@Override
	public ReceiveData doCommand(final Connection connection, int pid, String arg) {
		String[] args = arg.split(":");
		if (args[0].equals("OK") && args.length == 2) {
			final String version = args[1];
			connection.authorize();
			LogUtil.d("RemoteController", "Auth success.");
			LogUtil.d("RemoteController", "Server version:" + version);
			connection.getSession().getHandler().post(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(connection.getSession(), MainActivity.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean("RECOMMENDED_SERVER_VERSION", connection.getSession().getRecommendServerVersion().equals(version));
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					connection.getSession().dismissProgressDialog();
					connection.getSession().startActivity(intent);
				}
			});
		} else {
			connection.getSession().close(true, connection.getSession().getString(R.string.str_auth_error));
		}
		return null;
	}
}
