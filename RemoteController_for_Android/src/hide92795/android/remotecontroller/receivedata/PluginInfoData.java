package hide92795.android.remotecontroller.receivedata;


public class PluginInfoData extends ReceiveData {
	private String name;
	private String version;
	private String author;
	private String web;
	private String description;
	private boolean enabled;
	private CommandData[] commands;
	private PermissionData[] permissions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            セットする description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public CommandData[] getCommands() {
		return commands;
	}

	public void setCommands(CommandData[] command_datas) {
		this.commands = command_datas;
	}

	public PermissionData[] getPermissions() {
		return permissions;
	}

	public void setPermissions(PermissionData[] permission_datas) {
		this.permissions = permission_datas;
	}
}
