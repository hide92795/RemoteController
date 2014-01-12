package hide92795.android.remotecontroller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ConnectionConfig implements Serializable {
	private static final long serialVersionUID = -2068172464341212838L;
	private ArrayList<String> ids;
	private LinkedHashMap<String, ConnectionData> datas;

	public ConnectionConfig() {
		this(new ArrayList<String>(), new LinkedHashMap<String, ConnectionData>());
	}

	public ConnectionConfig(ArrayList<String> ids, LinkedHashMap<String, ConnectionData> datas) {
		this.ids = ids;
		this.datas = datas;
	}

	public ArrayList<String> getIds() {
		return ids;
	}

	public LinkedHashMap<String, ConnectionData> getDatas() {
		return datas;
	}
}
