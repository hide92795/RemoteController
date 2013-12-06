package net.arnx.jsonic.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LocalCache {
	private static final int CACHE_SIZE = 256;
	
	private ResourceBundle resources;
	private Locale locale;
	private TimeZone timeZone;
	
	private StringBuilder builderCache;
	private String[] stringCache;
	private BigDecimal[] numberCache;
	private Map<String, DateFormat> dateFormatCache;
	private Map<String, NumberFormat> numberFormatCache;
	
	public LocalCache(String bundle, Locale locale, TimeZone timeZone) {
		this.resources = ResourceBundle.getBundle(bundle, locale);
		this.locale = locale;
		this.timeZone = timeZone;
	}
	
	public StringBuilder getCachedBuffer() {
		if (builderCache == null) {
			builderCache = new StringBuilder();
		} else {
			builderCache.setLength(0);
		}
		return builderCache;
	}
	
	public String getString(StringBuilder sb) {
		if (sb.length() == 0) return "";
		
		if (sb.length() < 32) {
			int index = getCacheIndex(sb);
			if (index < 0) {
				return sb.toString();
			}
			
			if (stringCache == null) stringCache = new String[CACHE_SIZE];
			if (numberCache == null) numberCache = new BigDecimal[CACHE_SIZE];
			
			String str = stringCache[index];
			if (str == null || str.length() != sb.length()) {
				str = sb.toString();
				stringCache[index] = str;
				numberCache[index] = null;
				return str;
			}
			
			for (int i = 0; i < sb.length(); i++) {
				if (str.charAt(i) != sb.charAt(i)) {
					str = sb.toString();
					stringCache[index] = str;
					numberCache[index] = null;
					return str;
				}
			}
			return str;
		}
		
		return sb.toString();
	}
	
	public BigDecimal getBigDecimal(StringBuilder sb) {
		if (sb.length() == 1) {
			if (sb.charAt(0) == '0') {
				return BigDecimal.ZERO;
			} else if (sb.charAt(0) == '1') {
				return BigDecimal.ONE;
			}
		}
		
		if (sb.length() < 32) {
			int index = getCacheIndex(sb);
			if (index < 0) {
				return new BigDecimal(sb.toString());
			}
						
			if (stringCache == null) stringCache = new String[CACHE_SIZE];
			if (numberCache == null) numberCache = new BigDecimal[CACHE_SIZE];
			
			String str = stringCache[index];
			BigDecimal num = numberCache[index];
			if (str == null || str.length() != sb.length()) {
				str = sb.toString();
				num = new BigDecimal(str);
				stringCache[index] = str;
				numberCache[index] = num;
				return num;
			}
			
			for (int i = 0; i < sb.length(); i++) {
				if (str.charAt(i) != sb.charAt(i)) {
					str = sb.toString();
					num = new BigDecimal(str);
					stringCache[index] = str;
					numberCache[index] = num;
					return num;
				}
			}
			
			if (num == null) {
				num = new BigDecimal(str);
				numberCache[index] = num;
			}
			return num;
		}
		
		return new BigDecimal(sb.toString());
	}
	
	private int getCacheIndex(StringBuilder sb) {
		int h = 0;
		int max = Math.min(16, sb.length());
		for (int i = 0; i < max; i++) {
			h = h * 31 + sb.charAt(i);
		}
		return h & (CACHE_SIZE-1);
	}
	
	public NumberFormat getNumberFormat(String format) {
		NumberFormat nformat = null;
		if (numberFormatCache == null) {
			numberFormatCache = new HashMap<String, NumberFormat>();
		} else {
			nformat = numberFormatCache.get(format);
		}
		if (nformat == null) {
			nformat = new DecimalFormat(format, new DecimalFormatSymbols(locale));
			numberFormatCache.put(format, nformat);
		}
		return nformat;
	}
	
	public DateFormat getDateFormat(String format) {
		DateFormat dformat = null;
		if (dateFormatCache == null) {
			dateFormatCache = new HashMap<String, DateFormat>();
		} else {
			dformat = dateFormatCache.get(format);
		}
		if (dformat == null) {
			dformat = new ExtendedDateFormat(format, locale);
			dformat.setTimeZone(timeZone);
			dateFormatCache.put(format, dformat);
		}
		return dformat;
	}
	
	public String getMessage(String id) {
		return getMessage(id, (Object[])null);
	}
	
	public String getMessage(String id, Object... args) {
		if (args != null && args.length > 0) {
			return MessageFormat.format(resources.getString(id), args);
		} else {
			return resources.getString(id);
		}
	}
}
