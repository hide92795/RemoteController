package hide92795.android.remotecontroller.util;

public class StringUtils {
	public static String join(String with, Iterable<?> arry) {
		StringBuffer buf = new StringBuffer();
		for (Object s : arry) {
			if (buf.length() > 0) {
				buf.append(with);
			}
			buf.append(s);
		}
		return buf.toString();
	}

	public static String join(String with, Object... arry) {
		StringBuffer buf = new StringBuffer();
		for (Object s : arry) {
			if (buf.length() > 0) {
				buf.append(with);
			}
			buf.append(s);
		}
		return buf.toString();
	}

	public static boolean equals(String str1, String str2) {
		if (str1.length() != str2.length()) {
			return false;
		}
		return str1.equals(str2);
	}
}
