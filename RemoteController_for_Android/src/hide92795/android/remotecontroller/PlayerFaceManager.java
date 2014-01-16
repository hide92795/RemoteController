package hide92795.android.remotecontroller;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class PlayerFaceManager {
	private final ConcurrentHashMap<String, Bitmap> faces;
	private final Bitmap steve;

	public PlayerFaceManager(Context context) {
		this.faces = new ConcurrentHashMap<String, Bitmap>();
		this.steve = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.steve)).getBitmap();
	}

	public void addFace(String username, Bitmap data) {
		faces.put(username, data);
	}

	public void removeFace(String username) {
		faces.remove(username);
	}

	public Bitmap getFace(String username) {
		return faces.get(username);
	}

	public boolean existFace(String username) {
		return faces.containsKey(username);
	}

	public void addFaceDefault(String username) {
		faces.put(username, steve);
	}

	public void dispose() {
		Set<String> s = faces.keySet();
		for (String name : s) {
			Bitmap img = faces.get(name);
			img.recycle();
		}
		steve.recycle();
	}
}
