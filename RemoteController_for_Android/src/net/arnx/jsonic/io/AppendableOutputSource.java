package net.arnx.jsonic.io;

import java.io.IOException;

public class AppendableOutputSource implements OutputSource {
	private final Appendable ap;
	
	public AppendableOutputSource(Appendable ap) {
		this.ap = ap;
	}
	
	@Override
	public void append(String text) throws IOException {
		ap.append(text);
	}
	
	@Override
	public void append(String text, int start, int end) throws IOException {
		ap.append(text, start, end);
	}
	
	@Override
	public void append(char c) throws IOException {
		ap.append(c);
	}
	
	@Override
	public void flush() throws IOException {
	}
	
	@Override
	public String toString() {
		return ap.toString();
	}
}

