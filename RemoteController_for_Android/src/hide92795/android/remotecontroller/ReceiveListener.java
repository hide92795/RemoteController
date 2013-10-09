package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.receivedata.ReceiveData;

public interface ReceiveListener {
	void onReceiveData(String sended_cmd, int pid, ReceiveData data);
}
