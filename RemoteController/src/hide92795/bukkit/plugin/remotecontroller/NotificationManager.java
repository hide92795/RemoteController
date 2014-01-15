package hide92795.bukkit.plugin.remotecontroller;

import hide92795.bukkit.plugin.remotecontroller.notification.Notification;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationManager {
	private final RemoteController plugin;
	private final ConcurrentLinkedQueue<String> keys;
	private final ConcurrentHashMap<String, Notification> datas;

	public NotificationManager(RemoteController plugin) {
		this.plugin = plugin;
		this.keys = new ConcurrentLinkedQueue<>();
		this.datas = new ConcurrentHashMap<>();
	}

	public void addNotification(String uuid, Notification notification) {
		keys.add(uuid);
		datas.put(uuid, notification);
		if (keys.size() > plugin.config.notification_max) {
			datas.remove(keys.poll());
		}
	}

	public Notification[] getAll() {
		ArrayList<Notification> all = new ArrayList<>();
		for (String uuid : keys) {
			Notification notification = datas.get(uuid);
			all.add(notification);
		}
		return all.toArray(new Notification[all.size()]);
	}

	public void setNotificationState(String uuid, boolean set_value) {
		Notification n = datas.get(uuid);
		if (n != null) {
			n.setConsumed(set_value);
		} else {
			plugin.getLogger().warning("Notification not found.");
		}
	}

	public void markAsConsumedAll() {
		for (String uuid : keys) {
			setNotificationState(uuid, true);
		}
	}
}
