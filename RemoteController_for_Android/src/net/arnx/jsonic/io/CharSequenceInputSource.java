package net.arnx.jsonic.io;

public class CharSequenceInputSource implements InputSource {
	private int lines = 1;
	private int columns = 0;
	private int offset = 0;
	
	private int start = 0;
	int mark = -1;
	
	private final CharSequence cs;
	
	public CharSequenceInputSource(CharSequence cs) {
		if (cs == null) {
			throw new NullPointerException();
		}
		this.cs = cs;
	}
	
	@Override
	public int next() {
		int n = -1;
		if (start < cs.length()) {
			n = cs.charAt(start++);
			offset++;
			if (n == '\r') {
				lines++;
				columns = 0;
			} else if (n == '\n') {
				if (offset < 2 || cs.charAt(offset-2) != '\r') {
					lines++;
					columns = 0;
				}
			} else {
				columns++;
			}
		} else {
			start++;
			return -1;
		}
		return n;
	}
	
	@Override
	public void back() {
		if (start == 0) {
			throw new IllegalStateException("no backup charcter");
		}
		start--;
		if (start < cs.length()) {
			offset--;
			columns--;
		}
	}
	
	@Override
	public int mark() {
		mark = start;
		return cs.length() - mark;
	}
	
	@Override
	public void copy(StringBuilder sb, int len) {
		if (mark == -1) throw new IllegalStateException("no mark");
		if (mark + len > cs.length()) throw new IndexOutOfBoundsException();
		
		sb.append(cs, mark, mark + len);
	}
	
	@Override
	public String copy(int len) {
		if (mark == -1) throw new IllegalStateException("no mark");
		if (mark + len > cs.length()) throw new IndexOutOfBoundsException();
		
		char[] array = new char[len];
		for (int i = 0; i < len; i++) {
			array[i] = cs.charAt(mark + i);
		}
		return String.valueOf(array);
	}
	
	@Override
	public long getLineNumber() {
		return lines;
	}
	
	@Override
	public long getColumnNumber() {
		return columns;
	}
	
	@Override
	public long getOffset() {
		return offset;
	}
	
	@Override
	public String toString() {
		int spos = 0;
		int max = Math.min(start-1, cs.length()-1);
		int charCount = 0;
		for (int i = 0; i < max + 1 && i < 20; i++) {
			char c = cs.charAt(max-i);
			if (c == '\r' || (c == '\n' && (max-i-1 < 0 || cs.charAt(max-i-1) != '\r'))) {
				if (charCount > 0) break;
			} else if (c != '\n') {
				spos = max-i;
				charCount++;
			}
		}
		return (spos <= max) ? cs.subSequence(spos, max+1).toString() : "";
	}
}