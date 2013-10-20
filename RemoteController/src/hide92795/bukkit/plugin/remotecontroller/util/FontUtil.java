package hide92795.bukkit.plugin.remotecontroller.util;

import java.util.HashMap;

public class FontUtil {
	public static final String ESC_BOLD = "21";
	public static final String ESC_STRIKE = "9";
	public static final String ESC_UNDERLINE = "4";
	public static final String ESC_ITALIC = "3";

	public static final String BUKKIT_BOLD = "l";
	public static final String BUKKIT_STRIKE = "m";
	public static final String BUKKIT_UNDERLINE = "n";
	public static final String BUKKIT_ITALIC = "o";
	public static final String BUKKIT_MAGIC = "k";
	public static final String BUKKIT_RESET = "r";


	public static final HashMap<String, String> ESC_COLOR;
	public static final HashMap<String, String> BUKKIT_COLOR;
	static {
		ESC_COLOR = new HashMap<>();
		ESC_COLOR.put("0;30;22", "#000000");// black
		ESC_COLOR.put("0;34;22", "#00008B");// dark blue
		ESC_COLOR.put("0;32;22", "#006400");// dark green
		ESC_COLOR.put("0;36;22", "#05696B");// dark aqua
		ESC_COLOR.put("0;31;22", "#8B0000");// dark red
		ESC_COLOR.put("0;35;22", "#871F78");// dark purple
		ESC_COLOR.put("0;33;22", "#FFD700");// gold
		ESC_COLOR.put("0;37;22", "#808080");// gray
		ESC_COLOR.put("0;30;1", "#A9A9A9");// dark gray
		ESC_COLOR.put("0;34;1", "#0000FF");// blue
		ESC_COLOR.put("0;32;1", "#00FF00");// green
		ESC_COLOR.put("0;36;1", "#00FFFF");// aqua
		ESC_COLOR.put("0;31;1", "#FF0000");// red
		ESC_COLOR.put("0;35;1", "#FF00FF");// light purple
		ESC_COLOR.put("0;33;1", "#FFFF00");// yellow
		ESC_COLOR.put("0;37;1", "#FFFFFF");// white

		BUKKIT_COLOR = new HashMap<>();
		BUKKIT_COLOR.put("0", "#000000");// black
		BUKKIT_COLOR.put("1", "#00008B");// dark blue
		BUKKIT_COLOR.put("2", "#006400");// dark green
		BUKKIT_COLOR.put("3", "#05696B");// dark aqua
		BUKKIT_COLOR.put("4", "#8B0000");// dark red
		BUKKIT_COLOR.put("5", "#871F78");// dark purple
		BUKKIT_COLOR.put("6", "#FFD700");// gold
		BUKKIT_COLOR.put("7", "#808080");// gray
		BUKKIT_COLOR.put("8", "#A9A9A9");// dark gray
		BUKKIT_COLOR.put("9", "#0000FF");// blue
		BUKKIT_COLOR.put("a", "#00FF00");// green
		BUKKIT_COLOR.put("b", "#00FFFF");// aqua
		BUKKIT_COLOR.put("c", "#FF0000");// red
		BUKKIT_COLOR.put("d", "#FF00FF");// light purple
		BUKKIT_COLOR.put("e", "#FFFF00");// yellow
		BUKKIT_COLOR.put("f", "#FFFFFF");// white
	}

	public static StringBuilder convertEscapeSequence(String esc, String target) {
		StringBuilder sb = new StringBuilder();
		switch (esc) {
		case ESC_BOLD:
			sb.append("<b>");
			sb.append(target);
			sb.append("</b>");
			break;
		case ESC_STRIKE:
			sb.append("<del>");
			sb.append(target);
			sb.append("</del>");
			break;
		case ESC_UNDERLINE:
			sb.append("<u>");
			sb.append(target);
			sb.append("</u>");
			break;
		case ESC_ITALIC:
			sb.append("<i>");
			sb.append(target);
			sb.append("</i>");
			break;
		default:
			String html_color = ESC_COLOR.get(esc);
			if (html_color == null) {
				sb.append(target);
			} else {
				sb.append("<font color=\"");
				sb.append(html_color);
				sb.append("\">");
				sb.append(target);
				sb.append("</font>");
			}
			break;
		}
		return sb;
	}

	public static StringBuilder convertBukkitChatColor(String color, String target) {
		StringBuilder sb = new StringBuilder();
		switch (color) {
		case BUKKIT_BOLD:
			sb.append("<b>");
			sb.append(target);
			sb.append("</b>");
			break;
		case BUKKIT_STRIKE:
			sb.append("<del>");
			sb.append(target);
			sb.append("</del>");
			break;
		case BUKKIT_UNDERLINE:
			sb.append("<u>");
			sb.append(target);
			sb.append("</u>");
			break;
		case BUKKIT_ITALIC:
			sb.append("<i>");
			sb.append(target);
			sb.append("</i>");
			break;
		case BUKKIT_MAGIC:
		case BUKKIT_RESET:
			sb.append(target);
			break;
		default:
			String html_color = BUKKIT_COLOR.get(color);
			if (html_color == null) {
				sb.append(color);
				sb.append(target);
			} else {
				sb.append("<font color=\"");
				sb.append(html_color);
				sb.append("\">");
				sb.append(target);
				sb.append("</font>");
			}
			break;
		}
		return sb;
	}
}
