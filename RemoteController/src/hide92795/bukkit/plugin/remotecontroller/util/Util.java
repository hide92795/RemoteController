package hide92795.bukkit.plugin.remotecontroller.util;

import java.io.File;

public class Util {
	public static String removeEscapeSequence(String text) {
		return text.replaceAll("\\e\\[(\\d|;)*m", "").replaceAll("§.", "");
	}

	public static String[] toFileStringArray(File[] files) {
		int size = files.length;
		String[] files_s = new String[size];
		for (int i = 0; i < size; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				files_s[i] = "/" + file.getName();
			} else {
				files_s[i] = file.getName();
			}
		}
		return files_s;
	}

	public static String[] marge(String... strs) {
		return strs;
	}
}
