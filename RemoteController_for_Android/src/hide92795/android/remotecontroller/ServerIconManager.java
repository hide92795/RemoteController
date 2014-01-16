package hide92795.android.remotecontroller;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class ServerIconManager {

	private final ConcurrentHashMap<String, Bitmap> icons;
	private final Bitmap def;

	public ServerIconManager(Context context) {
		this.icons = new ConcurrentHashMap<String, Bitmap>();
		this.def = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
	}

	public void addServerIcon(String uuid, Bitmap data) {
		icons.put(uuid, data);
	}

	public void removeServerIcon(String uuid) {
		icons.remove(uuid);
	}

	public Bitmap getServerIcon(String uuid) {
		if (icons.containsKey(uuid)) {
			if (icons.get(uuid) == null) {
				return def;
			} else {
				return icons.get(uuid);
			}
		} else {
			return def;
		}
	}

	public boolean existServerIcon(String uuid) {
		return icons.containsKey(uuid);
	}

	public void addServerIconDefault(String uuid) {
		icons.put(uuid, def);
	}

	public void dispose() {
		Set<String> s = icons.keySet();
		for (String uuid : s) {
			Bitmap img = icons.get(uuid);
			img.recycle();
		}
		def.recycle();
	}

}
