package net.arnx.jsonic.io;

import java.io.IOException;

public interface OutputSource {
	public void append(String text) throws IOException;
	public void append(String text, int start, int end) throws IOException;
	public void append(char c) throws IOException;
	public void flush() throws IOException;
}

