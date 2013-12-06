package net.arnx.jsonic.io;

public class StringBuilderInputSource extends CharSequenceInputSource {
	private final StringBuilder sb;
	
	public StringBuilderInputSource(StringBuilder sb) {
		super(sb);
		this.sb = sb;
	}
	
	@Override
	public void copy(StringBuilder sb, int len) {
		if (mark == -1) throw new IllegalStateException("no mark");
		if (mark + len > this.sb.length()) throw new IndexOutOfBoundsException();
		
		sb.append(this.sb, mark, mark + len);
	}
	
	@Override
	public String copy(int len) {
		if (mark == -1) throw new IllegalStateException("no mark");
		if (mark + len > sb.length()) throw new IndexOutOfBoundsException();
		
		return sb.substring(mark, mark + len);
	}
}
