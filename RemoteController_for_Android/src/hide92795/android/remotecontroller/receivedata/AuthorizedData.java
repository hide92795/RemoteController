package hide92795.android.remotecontroller.receivedata;

public class AuthorizedData extends ReceiveData {
	private static final long serialVersionUID = -4656532145776728654L;
	private String server_bukkit_version;
	private String server_minecraft_version;

	public String getServerBukkitVersion() {
		return server_bukkit_version;
	}

	public void setServerBukkitVersion(String server_bukkit_version) {
		this.server_bukkit_version = server_bukkit_version;
	}

	public String getServerMinecraftVersion() {
		return server_minecraft_version;
	}

	public void setServerMinecraftVersion(String server_minecraft_version) {
		this.server_minecraft_version = server_minecraft_version;
	}
}
