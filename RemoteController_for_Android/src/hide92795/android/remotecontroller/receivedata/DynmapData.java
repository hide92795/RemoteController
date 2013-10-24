package hide92795.android.remotecontroller.receivedata;

public class DynmapData extends ReceiveData {
	private int port;
	private boolean enable;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
