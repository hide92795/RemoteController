package hide92795.android.remotecontroller.receivedata;

import java.io.Serializable;

public class ServerData extends ReceiveData implements Serializable {
	private static final long serialVersionUID = -8419428504778511317L;
	private String servername;
	private String address;
	private int max;
	private int current;
	private String add_info;

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
