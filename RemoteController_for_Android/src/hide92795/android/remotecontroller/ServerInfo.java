package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.receivedata.DynmapData;

public class ServerInfo {
	private DynmapData dynmap_data;
	private MinecraftVersion server_minecraft_version;

	public DynmapData getDynmapData() {
		return dynmap_data;
	}

	public void setDynmapData(DynmapData dynmap_data) {
		this.dynmap_data = dynmap_data;
	}

	public MinecraftVersion getServerMinecraftVersion() {
		return server_minecraft_version;
	}

	public void setServerMinecraftVersion(MinecraftVersion server_minecraft_version) {
		this.server_minecraft_version = server_minecraft_version;
	}
}
