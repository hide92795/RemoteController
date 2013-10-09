package hide92795.android.remotecontroller.command;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.receivedata.DirectoryData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.receivedata.DirectoryData.File;
import hide92795.android.remotecontroller.util.LogUtil;

public class CommandDirectory implements Command {
	public ReceiveData doCommand(Connection connection, int pid, String arg) {
		LogUtil.d("Directory", arg);
		String[] files_s = arg.split(":");
		DirectoryData data = new DirectoryData();
		String basedir = files_s[0];
		File[] files;
		if (basedir.length() == 0) {
			// Root
			data.setRoot(true);
			data.setDir("/");
			files = new File[files_s.length - 1];
			for (int i = 0; i < files.length; i++) {
				String file = files_s[i + 1];
				if (file.startsWith("/")) {
					// Directory
					files[i] = new File(file.replaceAll("/", ""), basedir + file, true);
				} else {
					// File
					files[i] = new File(file, basedir + "/" + file, false);
				}
			}
		} else {
			// Not Root
			data.setRoot(false);
			data.setDir(basedir);
			try {
				files = new File[files_s.length];
				files[0] = File.createUpDir(basedir);
				for (int i = 1; i < files.length; i++) {
					String file = files_s[i];
					if (file.startsWith("/")) {
						// Directory
						files[i] = new File(file.replaceAll("/", ""), basedir + file, true);
					} else {
						// File
						files[i] = new File(file, basedir + "/" + file, false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		data.setFiles(files);
		return data;
	}
}
