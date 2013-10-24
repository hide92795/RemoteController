package hide92795.bukkit.plugin.remotecontroller.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public class Util {
	private static Pattern esc_seq = Pattern.compile("\\[((?:\\d|;)*)m(.*)");

	public static String removeColorCode(String text) {
		return text.replaceAll("\\e\\[(\\d|;)*m", "").replaceAll("§.", "");
	}

	public static String convertColorCode(String text) {
		String convertedEscapeSeq = convertEscapeSequence(text);
		String convertedAll = convertBukkitChatColor(convertedEscapeSeq);
		return convertedAll;
	}

	public static String convertEscapeSequence(String text) {
		text = ChatColor.translateAlternateColorCodes('&', text);
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\n", "<br />");
		String[] spilit = text.split("\\e");
		StringBuilder sb = new StringBuilder();
		for (String s : spilit) {
			Matcher m = esc_seq.matcher(s);
			if (m.find()) {
				String esc = m.group(1);
				String target = m.group(2);
				if (esc == null || target == null || target.length() == 0) {
					continue;
				}
				sb.append(FontUtil.convertEscapeSequence(esc, target));
			} else {
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public static String convertBukkitChatColor(String text) {
		String[] spilit = text.split("§");
		StringBuilder sb = new StringBuilder();
		sb.append(spilit[0]);
		for (int i = 1; i < spilit.length; i++) {
			String s = spilit[i];
			if (s == null || s.length() == 0) {
				continue;
			}
			String color = String.valueOf(s.charAt(0));
			String target = s.substring(1);
			sb.append(FontUtil.convertBukkitChatColor(color, target));
		}
		return sb.toString();
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
