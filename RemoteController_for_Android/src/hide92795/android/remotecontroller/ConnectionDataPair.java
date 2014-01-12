package hide92795.android.remotecontroller;

public class ConnectionDataPair {
	public final String uuid;
	public final ConnectionData data;

	public ConnectionDataPair(String uuid, ConnectionData data) {
		this.uuid = uuid;
		this.data = data;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
