package hide92795.bukkit.plugin.remotecontroller.api;

import hide92795.bukkit.plugin.remotecontroller.RemoteController;

public class RemoteControllerAPI {

	private final RemoteController plugin;

	public RemoteControllerAPI(RemoteController plugin) {
		this.plugin = plugin;
	}

	public void registerCreator(AdditionalInfoCreator creator) {
		plugin.addAdditionalInfoCreator(creator);
	}
}
