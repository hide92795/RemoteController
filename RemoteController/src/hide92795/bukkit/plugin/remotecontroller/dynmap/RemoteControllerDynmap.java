package hide92795.bukkit.plugin.remotecontroller.dynmap;

import hide92795.bukkit.plugin.remotecontroller.RemoteController;
import java.lang.reflect.Field;
import org.dynmap.DynmapCore;
import org.dynmap.bukkit.DynmapPlugin;

public class RemoteControllerDynmap {
	public static int getPort(RemoteController plugin) throws Exception {
		DynmapPlugin dynmap = (DynmapPlugin) plugin.getServer().getPluginManager().getPlugin("dynmap");
		Field field_dynmap_core = dynmap.getClass().getDeclaredField("core");
		field_dynmap_core.setAccessible(true);
		DynmapCore dynmap_core = (DynmapCore) field_dynmap_core.get(dynmap);
		Field field_webport = dynmap_core.getClass().getDeclaredField("webport");
		field_webport.setAccessible(true);
		int webport = field_webport.getInt(dynmap_core);
		return webport;
	}
}
