package net.arnx.jsonic.io;

public class StringBuilderOutputSource implements OutputSource {
	private final StringBuilder sb;
	
	public StringBuilderOutputSource() {
		this.sb = new StringBuilder(1000);
	}
	
	public StringBuilderOutputSource(StringBuilder sb) {
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
