package hide92795.android.remotecontroller;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class ConnectionDataMigration {
	@SuppressWarnings("unchecked")
	public static ConnectionConfig v1Tov2(FileInputStream fis) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(fis);
		ArrayList<ConnectionData> old_datas = (ArrayList<ConnectionData>) ois.readObject();
		LinkedHashMap<String, ConnectionData> datas = new LinkedHashMap<String, ConnectionData>();
		ArrayList<String> ids = new ArrayList<String>();
		for (ConnectionData data : old_datas) {
			String uuid = UUID.randomUUID().toString();
			datas.put(uuid, data);
			ids.add(uuid);
		}
		ConnectionConfig cf = new ConnectionConfig(ids, datas);
		return cf;
	}
}
