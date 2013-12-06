/*
 * Copyright 2011 Hidekatsu Izuno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package net.arnx.jsonic;

public abstract class NamingStyle {
	public static final NamingStyle NOOP = new NamingStyle("NOOP") {
		@Override
		public String to(String value) {
			return value;
		}
	};
	
	public static final NamingStyle LOWER_CASE = new NamingStyle("LOWER_CASE") {
		@Override
		public String to(String value) {
			return toSimpleCase(value, false);
		}
	};
	
	public static final NamingStyle LOWER_CAMEL = new NamingStyle("LOWER_CAMEL") {
		@Override
		public String to(String value) {
			return toCamelCase(value, false);
		}
	};
	
	public static final NamingStyle LOWER_SPACE = new NamingStyle("LOWER_SPACE") {
		@Override
		public String to(String value) {
			return toSeparatedCase(value, false, ' ');
		}
	};
	
	public static final NamingStyle LOWER_HYPHEN = new NamingStyle("LOWER_HYPHEN") {
		@Override
		public String to(String value) {
			return toSeparatedCase(value, false, '-');
		}
	};
	
	public static final NamingStyle LOWER_UNDERSCORE = new NamingStyle("LOWER_UNDERSCORE") {
		@Override
		public String to(String value) {
			return toSeparatedCase(value, false, '_');
		}
	};
	
	public static final NamingStyle UPPER_CASE = new NamingStyle("UPPER_CASE") {
		@Override
		public String to(String value) {
			return toSimpleCase(value, true);
		}
	};
	
	public static final NamingStyle UPPER_CAMEL = new NamingStyle("UPPER_CAMEL") {
		@Override
		public String to(String value) {
			return toCamelCase(value, true);
		}
	};
	
	public static final NamingStyle UPPER_SPACE = new NamingStyle("UPPER_SPACE") {
		@Override
		public String to(String value) {
			return toSeparatedCase(value, true, ' ');
		}
	};
	
	public static final NamingStyle UPPER_HYPHEN = new NamingStyle("UPPER_HYPHEN") {
		@Override
		public String to(String value) {
			return toSeparatedCase(value, true, '-');
		}
	};
	
	public static final NamingStyle UPPER_UNDERSCORE = new NamingStyle("UPPER_UNDERSCORE") {
		@Override
		public String to(String value) {
			return toSeparatedCase(value, true, '_');
		}
	};
	
	private String name;
	
	public NamingStyle(String name) {
		this.name = name;
	}
	
	public abstract String to(String value);
	
	@Override
	public String toString() {
		return name;
	}
	
	private static final int SEPARATOR = 1;
	private static final int LOWER = 2;
	private static final int UPPER = 3;
	private static final int NUMBER = 4;
	private static final int OTHER = 9;
	private static final int[] MAP = new int[128];
	
	static {
		for (int i = 0; i < MAP.length; i++) {
			if (i >= 'A' && i <= 'Z') {
				MAP[i] = UPPER;
			} else if (i >= 'a' && i <= 'z') {
				MAP[i] = LOWER;
			} else if (i >= '0' && i <= '9') {
				MAP[i] = NUMBER;
			} else if (i == ' ' || i == '+'|| i == ',' || i == '-' || i == '.' || i == '_') {
				MAP[i] = SEPARATOR;
			} else {
				MAP[i] = OTHER;
			}
		}
	}
	
	private static String toSimpleCase(String value, boolean upper) {
		if (value == null || value.length() == 0) {
			return value;
		}
		char[] ca = value.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			int type = getType(ca[i]);
			if (upper && type == LOWER) {
				ca[i] = (char)(ca[i] - 32);
			} else if (!upper && type == UPPER) {
				ca[i] = (char)(ca[i] + 32);
			}
		}
		return String.valueOf(ca);
	}
	
	private static String toCamelCase(String value, boolean upper) {
		if (value == null || value.length() == 0) {
			return value;
		}
		
		int start;
		int end;
		for (start = 0; start < value.length(); start++) {
			int type = getType(value.charAt(start));
			if (type != SEPARATOR && type != OTHER) break;
		}
		for (end = 0; end < value.length()-start; end++) {
			int type = getType(value.charAt(value.length()-end-1));
			if (type != SEPARATOR && type != OTHER) break;
		}
		
		int index = -1;
		for (int i = start; i < value.length()-end; i++) {
			if (getType(value.charAt(i)) == SEPARATOR) {
				index = i;
				break;
			}
		}
		
		if (index == -1) {
			int type = getType(value.charAt(start));
			if (type == UPPER) {
				char[] ca = value.toCharArray();
				if (!upper) ca[start] = (char)(ca[start] + 32);
				for (int i = start+1; i < ca.length - end; i++) {
					if (getType(ca[i]) != UPPER) {
						break;
					} else if (i+1 < ca.length - end) {
						int next = getType(ca[i+1]);
						if (next != UPPER && next != NUMBER) {
							break;
						}
					}
					ca[i] = (char)(ca[i] + 32);	
				}
				return String.valueOf(ca);
			} else if (upper && type == LOWER) {
				char[] ca = value.toCharArray();
				ca[start] = (char)(ca[start] - 32);
				return String.valueOf(ca);
			} else {
				return value;
			}
		} else {
			char[] ca = value.toCharArray();
			int pos = start;
			for (int i = start; i < ca.length - end; i++) {
				if (getType(ca[i]) == SEPARATOR) {
					upper = true;
				} else if (upper) {
					ca[pos++] = (getType(ca[i]) == LOWER) ? (char)(ca[i] - 32) : ca[i];
					upper = false;
				} else {
					ca[pos++] = (getType(ca[i]) == UPPER) ? (char)(ca[i] + 32) : ca[i];
					upper = false;
				}
			}
			for (int i = 0; i < end; i++) {
				ca[pos++] = ca[ca.length-end];
			}
			return String.valueOf(ca, 0, pos);
		}		
	}
	
	private static String toSeparatedCase(String value, boolean upper, char sep) {
		if (value == null || value.length() == 0) {
			return value;
		}
		
		int start;
		int end;
		for (start = 0; start < value.length(); start++) {
			int type = getType(value.charAt(start));
			if (type != SEPARATOR && type != OTHER) break;
		}
		for (end = 0; end < value.length()-start; end++) {
			int type = getType(value.charAt(value.length()-end-1));
			if (type != SEPARATOR && type != OTHER) break;
		}
		
		StringBuilder sb = new StringBuilder((int)(value.length() * 1.5));
		if (start > 0) sb.append(value, 0, start);
		int prev = -1;
		for (int i = start; i < value.length() - end; i++) {
			char c = value.charAt(i);
			int type = getType(c);
			if (type == UPPER && prev != -1) {
				if (prev != UPPER && prev != SEPARATOR) {
					sb.append(sep);
				} else if (i+1 < value.length() - end) {
					int next = getType(value.charAt(i+1));
					if (next != UPPER && next != NUMBER && next != SEPARATOR) {
						sb.append(sep);
					}
				}
			}
			if (type == SEPARATOR) {
				if (prev != SEPARATOR) sb.append(sep);
			} else if (upper && type == LOWER) {
				sb.append((char)(c - 32));
			} else if (!upper && type == UPPER) {
				sb.append((char)(c + 32));
			} else {
				sb.append(c);
			}
			prev = type;
		}
		if (end > 0) sb.append(value, value.length()-end, value.length());
		return sb.toString();
	}
	
	private static int getType(char c) {
		return (c < MAP.length) ? MAP[c] : 0;
	}
}
