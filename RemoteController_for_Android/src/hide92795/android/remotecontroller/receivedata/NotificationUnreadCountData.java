package hide92795.android.remotecontroller.receivedata;

import java.io.Serializable;

public class NotificationUnreadCountData extends ReceiveData implements Serializable {
	private static final long serialVersionUID = 4803439712436745765L;
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
