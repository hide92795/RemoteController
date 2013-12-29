package hide92795.android.remotecontroller.receivedata;

public class CommandData extends ReceiveData implements PluginInfoBase {
	private String command;
	private String description;
	private String alias;
	private String permisson;
	private String usage;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAliases() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPermisson() {
		return permisson;
	}

	public void setPermisson(String permisson) {
		this.permisson = permisson;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}
}
