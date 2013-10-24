package hide92795.bukkit.plugin.remotecontroller.api;

import hide92795.bukkit.plugin.remotecontroller.util.Util;

public class AdditionalInfo {
	private final String name;
	private final String data;

	public AdditionalInfo(String name, String data) {
		this.name = name;
		this.data = Util.removeColorCode(data);
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name);
		sb.append(":");
		sb.append(data);
		return sb.toString();
	}
}
