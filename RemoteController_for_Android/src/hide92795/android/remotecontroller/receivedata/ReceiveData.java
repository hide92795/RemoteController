package hide92795.android.remotecontroller.receivedata;

import java.io.Serializable;

public abstract class ReceiveData implements Serializable {
	private static final long serialVersionUID = 7973247561909131841L;
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
