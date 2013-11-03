package hide92795.android.remotecontroller.util;

import java.security.SecureRandom;

public class StringUtils {
	final static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

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

	public static String getRandomString(int cnt) {
		SecureRandom rnd = new SecureRandom();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cnt; i++) {
			int val = rnd.nextInt(CHARS.length());
			buf.append(CHARS.charAt(val));
		}
		return buf.toString();
	}
}
