package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.Items.ItemData;
import android.util.Log;


public enum MinecraftVersion {
	MC1_6_2, MC1_6_4, MC1_7_2, NOT_DELETED, SHOW_ALL;

	public static boolean canShow(MinecraftVersion server_version, ItemData item) {
		if (server_version == SHOW_ALL) {
			return true;
		}
		if (server_version.ordinal() >= item.added_version.ordinal()) {
			if (server_version.ordinal() < item.deleted_version.ordinal()) {
				return true;
			}
		}
		return false;
	}

	public static MinecraftVersion getByVersion(String version) {
		try {
			MinecraftVersion v = MinecraftVersion.valueOf("MC" + version.replaceAll("\\.", "_"));
			return v;
		} catch (Exception e) {
			Log.e("RemoteController:MinecraftVersion", "Unknown Minecraft version: " + version);
			return SHOW_ALL;
		}
	}
}
