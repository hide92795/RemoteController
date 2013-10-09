package hide92795.android.remotecontroller.receivedata;

public abstract class ReceiveData {
	private boolean success;

	public ReceiveData() {
		success = true;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSuccessed() {
		return success;
	}

}
