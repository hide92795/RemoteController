package hide92795.android.remotecontroller;

public class InitialReceive {
	public boolean server_info;
	public boolean dynmap;

	public boolean isAllReceived() {
		if (!server_info) {
			return false;
		}
		if (!dynmap) {
			return false;
		}
		return true;
	}

}
