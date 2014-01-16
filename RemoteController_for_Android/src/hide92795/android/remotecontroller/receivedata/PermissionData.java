package hide92795.android.remotecontroller.receivedata;


public class PermissionData extends ReceiveData implements PluginInfoBase {
	private static final long serialVersionUID = -8710519756480125523L;
	private String permission;
	private String description;

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
