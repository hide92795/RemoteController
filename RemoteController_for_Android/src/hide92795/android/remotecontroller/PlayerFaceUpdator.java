package hide92795.android.remotecontroller;

import hide92795.android.remotecontroller.util.LogUtil;
import java.io.InputStream;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class PlayerFaceUpdator extends AsyncTask<String, Void, Void> {
	private final Callback callback;
	private final PlayerFaceManager face_manager;
	private final String url;



	public PlayerFaceUpdator(Callback callback, PlayerFaceManager face_manager, String url) {
		this.callback = callback;
		this.face_manager = face_manager;
		this.url = url;
	}

	@Override
	protected Void doInBackground(String... params) {
		for (String username : params) {
			if (face_manager.existFace(username)) {
				LogUtil.d("", "Skip loading skin. Username: \"" + username + "\"");
				continue;
			}
			LogUtil.d("", "Start loading skin. Username: \"" + username + "\"");
			Bitmap image = null;
			try {
				URL url = new URL(this.url.replace("%player%", username));
				InputStream inputStream = url.openStream();
				image = BitmapFactory.decodeStream(inputStream);
			} catch (Exception e) {
				LogUtil.d("", "An error has occurred loading skin. Username: \"" + username + "\", Reason : " + e);
				face_manager.addFaceDefault(username);
				continue;
			}
			if (image == null) {
				continue;
			}
			if ((image.getHeight() < 32) || (image.getWidth() < 64)) {
				image.recycle();
				continue;
			}

			int[] face = new int[64];
			int[] face_accessory = new int[64];

			image.getPixels(face, 0, 8, 8, 8, 8, 8);
			image.getPixels(face_accessory, 0, 8, 40, 8, 8, 8);

			boolean transparent = false;
			int f = face_accessory[0];
			for (int i = 0; i < 64; i++) {
				if ((face_accessory[i] & 0xFF000000) == 0) {
					transparent = true;
					break;
				} else if (face_accessory[i] != f) {
					transparent = true;
					break;
				}
			}
			if (transparent) {
				for (int i = 0; i < 64; i++) {
					if ((face_accessory[i] & 0xFF000000) != 0)
						face[i] = face_accessory[i];
				}
			}
			Bitmap bitmap_8x8 = Bitmap.createBitmap(face, 8, 8, Bitmap.Config.ARGB_8888);
			Bitmap bitmap_48x48 = Bitmap.createScaledBitmap(bitmap_8x8, 48, 48, false);

			face_manager.addFace(username, bitmap_48x48);

			bitmap_8x8.recycle();
			image.recycle();

			publishProgress();

		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		callback.onProgress();
	}

	@Override
	protected void onPostExecute(Void result) {
		callback.onFinish();
	}

	public interface Callback {
		void onProgress();

		void onFinish();
	}
}
