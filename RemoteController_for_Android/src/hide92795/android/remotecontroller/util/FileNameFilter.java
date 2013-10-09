package hide92795.android.remotecontroller.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.InputFilter;
import android.text.Spanned;

public class FileNameFilter implements InputFilter {
	private static final String REGEX = "\\|/|:|\\*|\\?|\"|<|>|\\|";
	private static final Pattern PATTERN = Pattern.compile(REGEX);

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		String destStr = dest.toString();
		String newValue = destStr.substring(0, dstart) + source + destStr.substring(dend);
		Matcher m = PATTERN.matcher(newValue);
		if (m.find()) {
			return "";
		}
		return source;
	}

}
