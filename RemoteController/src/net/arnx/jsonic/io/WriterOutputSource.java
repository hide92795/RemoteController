package net.arnx.jsonic.io;

import java.io.IOException;
import java.io.Writer;

public class WriterOutputSource implements OutputSource {
	private final Writer writer;
	
	private final char[] buf = new char[1000];
	private int pos = 0;
	
	public WriterOutputSource(Writer writer) {
		this.writer = writer;
	}
	
	@Override
	public void append(String text) throws IOException {
		append(text, 0, text.length());
	}
	
	@Override
	public void append(String text, int start, int end) throws IOException {
		int length = end-start;
		if (pos + length < buf.length) {
			text.getChars(start, end, buf, pos);
			pos += length;
		} else {
			writer.write(buf, 0, pos);
			if (length < buf.length) {
				text.getChars(start, end, buf, 0);
				pos = length;
			} else {
				writer.write(text, start, length);
				pos = 0;
			}
		}
	}
	
	@Override
	public void append(char c) throws IOException {
		if (pos + 1 >= buf.length) {
			writer.write(buf, 0, pos);
			pos = 0;
		}
		buf[pos++] = c;
	}
	
	public void flush() throws IOException {
		if (pos > 0) writer.write(buf, 0, pos);
		writer.flush();
	}
}

