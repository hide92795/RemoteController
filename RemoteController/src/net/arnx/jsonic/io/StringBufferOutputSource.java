package net.arnx.jsonic.io;

public class StringBufferOutputSource implements OutputSource {
	private final StringBuffer sb;
	
	public StringBufferOutputSource() {
		this.sb = new StringBuffer(1000);
	}
	
	public StringBufferOutputSource(StringBuffer sb) {
		this.sb = sb;
	}
	
	@Override
	public void append(String text) {
		sb.append(text);
	}
	
	@Override
	public void append(String text, int start, int end) {
		sb.append(text, start, end);
	}
	
	@Override
	public void append(char c) {
		sb.append(c);
	}
	
	@Override
	public void flush() {
	}
	
	public void clear() {
		sb.setLength(0);
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
}
