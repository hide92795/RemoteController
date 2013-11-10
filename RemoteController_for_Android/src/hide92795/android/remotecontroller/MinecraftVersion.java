package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.Items.ItemData;
import android.util.Log;


public enum MinecraftVersion {
	MORE_OLDER, MC1_1, MC1_2_1, MC1_2_4, MC1_2_5, MC1_3_1, MC1_3_2, MC1_4_2, MC1_4_5, MC1_4_6, MC1_4_7, MC1_5, MC1_5_1, MC1_5_2, MC1_6_1, MC1_6_2, MC1_6_4, MC1_7_2, NOT_DELETED, SHOW_ALL;

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
