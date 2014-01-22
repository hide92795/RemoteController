package hide92795.android.remotecontroller.widget;

import java.io.Serializable;

public class WidgetData implements Serializable {
	private static final long serialVersionUID = 6444456503691659176L;
	private String account_uuid;
	private int background_color;
	private int font_color;

	public String getAccountUUID() {
		return account_uuid;
	}

	public void setAccountUUID(String account_uuid) {
		this.account_uuid = account_uuid;
	}

	public int getBackgroundColor() {
		return background_color;
	}

	public void setBackgroundColor(int background_color) {
		this.background_color = background_color;
	}

	public int getFontColor() {
		return font_color;
	}

	public void setFontColor(int font_color) {
		this.font_color = font_color;
	}

	public int getBackGroungAlpha() {
		return (background_color & 0xFF000000) >> 24;
	}
}
