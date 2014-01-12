package hide92795.android.remotecontroller.config;

import java.io.Serializable;
import java.util.HashSet;

public class AutoUpdateConfig implements Serializable {
	private static final long serialVersionUID = 2700962411824714616L;
	private HashSet<String> auto_update;

	public AutoUpdateConfig() {
		this.auto_update = new HashSet<String>();
	}

	public HashSet<String> getAutoUpdateList() {
		return auto_update;
	}
}
