package hide92795.android.remotecontroller.receivedata;

import hide92795.android.remotecontroller.util.StringUtils;
import java.io.Serializable;

public class FileData extends ReceiveData implements Serializable, Cloneable {
	private static final long serialVersionUID = 676087657447518156L;
	private String file;
	private String encoding;
	private String data;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public FileData clone() {
		FileData clone = new FileData();
		clone.setFile(file);
		clone.setEncoding(new String(encoding));
		clone.setData(new String(data));
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FileData) {
			FileData data = (FileData) o;
			if (data.file.equals(this.file)) {
				if (data.encoding.equals(this.encoding)) {
					if (data.data.equals(this.data)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return StringUtils.join(":", file, encoding, data);
	}
}
