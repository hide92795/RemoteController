package net.arnx.jsonic.io;

public class StringInputSource extends CharSequenceInputSource {
	private final String str;
	
	public StringInputSource(String str) {
		super(str);
		this.str = str;
	}
	
	@Override
	public void copy(StringBuilder sb, int len) {
		if (mark == -1) {
			throw new IllegalStateException("no mark");
		}
		sb.append(str, mark, mark + len);
	}
}
