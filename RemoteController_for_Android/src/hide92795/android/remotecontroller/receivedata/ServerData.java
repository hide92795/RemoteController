package hide92795.android.remotecontroller.receivedata;


public class ServerData extends ReceiveData {
	private String servername;
	private int port;
	private int max;
	private int current;
	private String add_info;

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getAddInfo() {
		return add_info;
	}

	public void setAddInfo(String add_info) {
		this.add_info = add_info;
	}


}
