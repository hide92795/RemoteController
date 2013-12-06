package net.arnx.jsonic.util;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExtendedDateFormat extends SimpleDateFormat {
	private static final long serialVersionUID = 1L;
	boolean escape = false;

	public ExtendedDateFormat(String pattern, Locale locale) {
		super(escape(pattern), locale);
		escape = !pattern.equals(this.toPattern());
	}

	public ExtendedDateFormat(String pattern) {
		super(escape(pattern), Locale.getDefault());
		escape = !pattern.equals(this.toPattern());
	}

	static String escape(String pattern) {
		boolean skip = false;
		int count = 0;
		StringBuilder sb = null;
		int last = 0;
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			if (c == '\'') {
				skip = !skip;
			} else if (c == 'Z' && !skip) {
				count++;
				if (count == 2) {
					if (sb == null)
						sb = new StringBuilder(pattern.length() + 4);
					sb.append(pattern, last, i - 1);
					sb.append("Z\0");
					last = i + 1;
				}
			} else {
				count = 0;
			}
		}
		if (sb != null) {
			if (last < pattern.length())
				sb.append(pattern, last, pattern.length());
			return sb.toString();
		} else {
			return pattern;
		}
	}

	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
		super.format(date, toAppendTo, pos);
		if (escape) {
			for (int i = 5; i < toAppendTo.length(); i++) {
				if (toAppendTo.charAt(i) == '\0') {
					toAppendTo.setCharAt(i, toAppendTo.charAt(i - 1));
					toAppendTo.setCharAt(i - 1, toAppendTo.charAt(i - 2));
					toAppendTo.setCharAt(i - 2, ':');
				}
			}
		}
		return toAppendTo;
	}
}
