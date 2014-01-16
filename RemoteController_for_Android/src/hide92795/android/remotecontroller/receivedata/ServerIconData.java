package hide92795.android.remotecontroller.receivedata;

import android.graphics.Bitmap;

public class ServerIconData extends ReceiveData {
	private static final long serialVersionUID = 2957938227857903680L;
	private Bitmap icon;

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
}
