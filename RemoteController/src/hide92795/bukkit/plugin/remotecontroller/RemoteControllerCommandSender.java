package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.util.Util;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class RemoteControllerCommandSender implements RemoteConsoleCommandSender {
	private final PermissibleBase perm = new PermissibleBase(this);
	private ClientConnection connection;
	private String name;

	public RemoteControllerCommandSender(ClientConnection connection, String user, int id) {
		this.connection = connection;
		this.name = "RemoteController-" + user + " ID:" + id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void sendMessageToRemote(String message) {
		connection.send("CONSOLE", 0, "-[Message]-" + Util.convertColorCode(message));
	}

	@Override
	public void sendMessage(String message) {
		sendMessageToRemote(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		for (String message : messages) {
			sendMessageToRemote(message);
		}
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean arg0) {
		throw new UnsupportedOperationException("Cannot change operator status of RemoteController command sender.");
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	public boolean isPermissionSet(String name) {
		return perm.isPermissionSet(name);
	}

	public boolean isPermissionSet(Permission perm) {
		return this.perm.isPermissionSet(perm);
	}

	public boolean hasPermission(String name) {
		return perm.hasPermission(name);
	}

	public boolean hasPermission(Permission perm) {
		return this.perm.hasPermission(perm);
	}

	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return perm.addAttachment(plugin, name, value);
	}

	public PermissionAttachment addAttachment(Plugin plugin) {
		return perm.addAttachment(plugin);
	}

	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return perm.addAttachment(plugin, name, value, ticks);
	}

	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return perm.addAttachment(plugin, ticks);
	}

	public void removeAttachment(PermissionAttachment attachment) {
		perm.removeAttachment(attachment);
	}

	public void recalculatePermissions() {
		perm.recalculatePermissions();
	}

	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return perm.getEffectivePermissions();
	}
}
