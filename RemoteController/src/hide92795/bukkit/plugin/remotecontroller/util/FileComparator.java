package hide92795.bukkit.plugin.remotecontroller.util;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;

public class FileComparator implements Comparator<File> {

	@Override
	public int compare(File arg0, File arg1) {
		if (arg0.isDirectory() && arg1.isFile()) {
			return -1;
		} else if (arg0.isFile() && arg1.isDirectory()) {
			return 1;
		} else {
			return arg0.getName().toLowerCase(Locale.getDefault()).compareTo(arg1.getName().toLowerCase(Locale.getDefault()));
		}
	}

}
