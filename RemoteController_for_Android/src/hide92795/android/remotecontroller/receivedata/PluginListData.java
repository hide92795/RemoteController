package hide92795.android.remotecontroller.receivedata;

import java.io.Serializable;

public class PluginListData extends ReceiveData {
	private static final long serialVersionUID = -6211219175028311896L;
	private PluginData[] plugins;

	public PluginData[] getPlugins() {
		return plugins;
	}

	public void setPlugins(PluginData[] plugins) {
		this.plugins = plugins;
	}

	public static class PluginData implements Serializable {
		private static final long serialVersionUID = 2733183211197066370L;
		private String name;
		private boolean enable;

		public PluginData(String name, boolean enable) {
			this.name = name;
			this.enable = enable;
		}

		public String getName() {
			return name;
		}

		public boolean isEnable() {
			return enable;
		}
	}
}
