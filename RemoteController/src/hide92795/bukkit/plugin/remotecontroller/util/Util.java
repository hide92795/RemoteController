package hide92795.bukkit.plugin.remotecontroller.util;

import hide92795.bukkit.plugin.remotecontroller.Modifiable;
import hide92795.bukkit.plugin.remotecontroller.org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

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
		try {
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
		} catch (Exception e) {
			return text;
		}
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

	public static LinkedHashMap<String, LinkedHashMap<String, String>> permissionsToHashMap(List<Permission> permissions) {
		LinkedHashMap<String, LinkedHashMap<String, String>> datas = new LinkedHashMap<>();
		for (Permission permission : permissions) {
			LinkedHashMap<String, String> desc = new LinkedHashMap<>();
			desc.put("DESCRIPTION", permission.getDescription());
			datas.put(permission.getName(), desc);
		}
		return datas;
	}

	public static LinkedHashMap<String, LinkedHashMap<String, String>> commandsToHashMap(Map<String, Map<String, Object>> commands) {
		LinkedHashMap<String, LinkedHashMap<String, String>> datas = new LinkedHashMap<>();
		if (commands != null) {
			for (String command : commands.keySet()) {
				LinkedHashMap<String, String> command_map = new LinkedHashMap<>();
				Map<String, Object> command_desc = commands.get(command);
				Object aliases = command_desc.get("aliases");
				if (aliases instanceof String) {
					command_map.put("ALIASES", (String) command_desc.get("aliases"));
				} else {
					command_map.put("ALIASES", StringUtils.join((List<?>) command_desc.get("aliases"), ", "));
				}
				command_map.put("PERMISSION", (String) command_desc.get("permission"));
				command_map.put("DESCRIPTION", (String) command_desc.get("description"));
				command_map.put("USAGE", (String) command_desc.get("usage"));
				datas.put(command, command_map);
			}
		}
		return datas;
	}

	public static File createFile(File root, String path) {
		String[] dirs = path.split(File.pathSeparator);
		File file = root;
		for (String dir : dirs) {
			file = new File(file, dir);
		}
		return file;
	}

	public static boolean canRead(LinkedHashMap<String, Modifiable> exclude_files, File file) throws IOException {
		boolean read = true;
		String target = file.getCanonicalPath();
		Set<String> s = exclude_files.keySet();
		for (String path : s) {
			if (target.contains(path)) {
				read = exclude_files.get(path).canRead();
			}
		}
		return read;
	}

	public static boolean canWrite(LinkedHashMap<String, Modifiable> exclude_files, File file) throws IOException {
		boolean read = true;
		String target = file.getCanonicalPath();
		Set<String> s = exclude_files.keySet();
		for (String path : s) {
			if (target.contains(path)) {
				read = exclude_files.get(path).canWrite();
			}
		}
		return read;
	}
}
