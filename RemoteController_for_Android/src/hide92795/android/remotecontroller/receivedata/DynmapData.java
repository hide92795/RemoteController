package hide92795.android.remotecontroller.receivedata;

public class DynmapData extends ReceiveData {
	private static final long serialVersionUID = -2785594714568800316L;
	private String address;
	private boolean enable;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
